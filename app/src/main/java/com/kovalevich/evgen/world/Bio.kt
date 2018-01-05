package com.kovalevich.evgen.world

import android.graphics.*
import java.util.*

class Bio(private val dnk: Dnk, coordinates: Point, val world: World): MapObject(coordinates,world) {

    // энегия. когда энегия заканчивается юнит умирает
    var energy = Settings.START_ENERGY
        set(value) {
            field = value
            if(value <= 0) dead()
            if(value > Settings.MAX_ENERGY) field = Settings.MAX_ENERGY
        }

    // сила юнита увеличивается с каждым убийством
    var power = Settings.START_POWER

    // коэфиициент мощности юнита, зависит от силы и накопленной энергии
    // определяет кто победит в поединке
    val strength: Double
        get() = power * Math.sqrt(energy.toDouble())

    // таймер сна
    var sleepTimer: Int = 0
        set(value) {
            field = value
            if(value < 0) field = 0
        }

    /* колония юнита
    * может быть null если не состоит в колонии */
    private var colony: Colony? = null

    /* текущее направление инициализируется рандомно
    * 0 - вверх
    * 1 - верх-право
    * 2 - низ-право
    * 3 - вниз
    * 4 - низ-лево
    * 5 - верх-лево
    */
    var direct = (0..5).random()
        set(value) {
            field = value
            if (value < 0) field = 5
            if (value > 5) field = 0
        }

    /* индикатор следующего action
     * инкриментируется при каждом ходе
     */
    private var currentAction = 0
        set(value) {
            field = value
            if (value < 0) field = Settings.NUMBER_OF_BEHAVIOR_ITEM
            if (value > Settings.NUMBER_OF_BEHAVIOR_ITEM) field = 0
        }

    /* цвет
    * меняется от красного в зависимости от силы юнита */
    override val color: Int
        get() {
            if(colony is Colony) return (colony as Colony).color
            return Color.RED + power * 10
        }

    override val strokeWidth: Float
        get() = super.strokeWidth + 1

    override fun action(): Boolean {

        /* если спит прибавляет по 10 энергии за ход */
        if(sleepTimer-- > 0) {
            energy += 10
            return super.action()
        }

        /* в зависимости от текущего номера экшена выполняем действие из набора
        все действия по умолчанию выполняются по направлению юнита direct
         */
        when(dnk.behavior[currentAction++]) {
            0 -> left() // поворот налево
            1 -> right() // поворот направо
            2 -> step() // сделать шаг
            3 -> attack() // атаковать
            4 -> photosynthesis() // фотосинтез
            5 -> synthesisOfMinerals() // синтез минералов
            6 -> explore() // исследовать окресность
            7 -> symbiosis() // симбиоз
            8 -> eat() // кушать
            9 -> setTrap() // поставить ловушку
            10 -> neutralizeTrap() // нейтрализовать ловушку
            11 -> division() // деление клетки
            12 -> findExit() // поиск выхода и выход
            13 -> sleep() // сон
            14 -> hunt() // охота
            else -> return false
        }

        energy--

        return super.action()
    }

    /* охотимся на юнитов в соседних клетках
    * нападаем на слабых или спящих */
    private fun hunt(aroundObjects: List<Any?> = world.map.getAroundObjects(coordinates)): Boolean {
        val target = aroundObjects.find { it is Bio && (this > it || it.sleepTimer > 0) }
        if (target is Bio) {
            attack(target)
            return true
        }
        return false
    }

    /* юнит засыпает на определенное число ходов
    * во время сна юнит накапливает энегию, но становится уязвимым */
    private fun sleep() {
        sleepTimer = Settings.SLEEP_TIME
    }

    /* ищем выход и идем
    * если выхода нет пробуем найти вокруг органику и съесть
    * если нет ищем слабого юнита и убить его
    * если нет ищем открытые ловушки */
    private fun findExit(): Boolean {

        val aroundObjects = world.map.getAroundObjects(coordinates)

        val exit = aroundObjects.find { it is Empty }
        if (exit is Empty) {
            step(exit)
            return true
        }

        val organic = aroundObjects.find { it is Organic }
        if (organic is Organic) {
            eat(organic)
            return true
        }

        hunt(aroundObjects)

        left()
        return false
    }

    /* деление юнита, если хватает энергии */
    private fun division(): Boolean {
        if(energy <= Settings.CHILD_ENERGY) return false

        val emptyObj = world.map.getAroundObjects(coordinates).find { it is Empty }
        if (emptyObj is Empty) {
            world.map.addObject(Bio(dnk.clone(), emptyObj.coordinates, world))
            world.countChildrens++
            energy -= Settings.CHILD_ENERGY
        }
        else return false
        return true
    }

    private fun neutralizeTrap(trap: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean { // нейтрализовать ловушку
        if (trap !is Trap) return false
        if(colony is Colony && !(colony as Colony).checkDistance(trap))
            return false

        if (trap.visible && dnk.hasSkill(3)) { // если ловушку видно и есть навык работы с ловушками, то обезвреживаем
            coordinates = trap.coordinates
            world.map.deleteObject(trap.coordinates)
            energy -= Settings.TRAP_ENERGY
            teachOthers(3)
        }
        else { // иначе умираем
            trap.visible = true
            dead()
        }

        return true
    }

    private fun setTrap(): Boolean { // поставить ловушку

        val directObject = world.map.getDirectObject(direct, coordinates)
        if (directObject !is Empty || !dnk.hasSkill(3)) return false

        energy -= Settings.TRAP_ENERGY
        world.map.addObject(Trap(directObject.coordinates, world))
        teachOthers(3)

        return true
    }

    private fun eat(target: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean { // кушать
        if(!dnk.hasSkill(1) || target == null) return false
        if(colony is Colony && !(colony as Colony).checkDistance(target))
            return false

        direct = getDirectToObject(coordinates, target.coordinates)
        when(target) {
            is Bio -> attack(target)
            is Poison -> neutralizePoison()
            is Organic -> {
                energy += target.energy
                world.map.deleteObject(target.coordinates)
                teachOthers(1)
                step(Empty(target.coordinates,world))
            }
            is Empty -> step(target)
            is Trap -> dead()
        }

        return true
    }

    /* симбиоз юнитов
    * попытка создать или присоедениться к колонии
    * или присоеденить юнита к колонии
    * если есть навык симбиоза */
    private fun symbiosis(target: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean { // симбиоз
        if(target !is Bio || !target.dnk.hasSkill(6) || !dnk.hasSkill(6)) return false

        var cn = Colony(world)
        if(colony is Colony) cn = colony as Colony
        if(target.colony is Colony) cn = target.colony as Colony

        joinToColony(cn)
        target.joinToColony(cn)

        return true
    }

    /* присоединение к колонии*/
    private fun joinToColony(cn: Colony): Boolean {
        if (colony is Colony) return true

        cn.addBio(this)
        colony = cn

        return true
    }

    /* выход из колонии*/
    private fun outFromColony(): Boolean{
        if(colony is Colony) {
            (colony as Colony).deleteBio(this)
            colony = null
        }
        return true
    }

    private fun explore(aroundObjects: List<Any?> = world.map.getAroundObjects(coordinates)): Boolean { // исследовать окресность

        // запоминаем количество энегии и будем обследовать соседей, пока не пополним запас
        val beforeEnergy = energy

        aroundObjects.forEach {
            when(it) {
                is Organic -> eat(it)
                is Bio -> if (dnk.aggression()) attack(it)
                is Trap -> neutralizeTrap(it)
                is Poison -> neutralizePoison(it)
            }
            if(energy > beforeEnergy) return true
        }

        val emptyObj = aroundObjects.find { it is Empty }
        if(emptyObj is Empty) step(emptyObj)

        return true
    }

    private fun synthesisOfMinerals(): Boolean {
        return false
    }

    private fun photosynthesis() { // фотосинтез
        energy += Settings.PHOTOSYNTHESIS_ENERGY
    }

    private fun attack(target: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean { // кушать
        if(!dnk.hasSkill(2) || target == null) return false
        if(colony is Colony && !(colony as Colony).checkDistance(target))
            return false

        direct = world.map.getDirectToObject(this, target)
        when(target) {
            is Bio -> {
                if(this == target && target.sleepTimer == 0) {
                    dead()
                    target.dead()
                    world.countKills += 2
                    return true
                }

                target.dnk.character++ // увеличение агрессии цели

                if (target > this  && target.sleepTimer == 0) {
                    target.attack(this)
                    return false
                }

                /*
                * расходуем энергию на убийство, если уель не спит
                * увеличиваем мощность юнита
                * увеличиваем счетчик убийств
                * умирает цель
                * если есть навык, кушаем добычу и учим другихтак делать*/
                if(target.sleepTimer != 0)
                    energy -= energy - (strength - target.strength).toInt()
                power++
                world.countKills++
                target.dead()
                teachOthers(2)

                if(dnk.hasSkill(7)) {
                    eat()
                    teachOthers(7)
                }
            }
            is Poison -> neutralizePoison(target)
            is Organic -> eat(target)
            is Empty -> step(target)
            is Trap -> neutralizeTrap(target)
        }

        return true
    }

    private fun neutralizePoison(poison: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean {
        if (poison !is Poison) return false

        if (dnk.hasSkill(5)) { // если есть навык работы с ядами, то обезвреживаем
            coordinates = poison.coordinates
            world.map.deleteObject(poison.coordinates)

            teachOthers(5)
        }
        else { // иначе умираем
            dead()
        }

        return true
    }

    /* сделать шаг вперед, или на переданный объект
    * если объект био, атакуем
    * если ловушка - смерть
    * если яд - смерть */
    private fun step(directObj: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean { // шаг вперед
        /* если впереди стенка или отрыв от колонии
        * то ничего не делаем*/
        if (directObj == null) return false

        if(colony is Colony && !(colony as Colony).checkDistance(directObj))
            return false

        direct = world.map.getDirectToObject(this, directObj)
        when(directObj){
            is Bio -> { // если по направлению движения находится другой юнит действуем по характеру
                if(dnk.aggression()) attack(directObj)
            }
            is Trap -> dead()
            is Empty -> coordinates = directObj.coordinates
            is Poison -> dead()
        }
        energy -= Settings.STEP_ENERGY

        return true
    }

    private fun right() { // повернуться направо
        direct++
    }

    private fun left() { // повернуться налево
        direct--
    }

    private fun teachOthers(skill: Int) { // обучение окружающих юнитов
        val aroundObjects = world.map.getAroundObjects(coordinates).filter { it is Bio }
        aroundObjects.forEach { (it as Bio).dnk.addSkill(skill) }
    }

    override fun draw(canvas: Canvas, paint: Paint, center: PointF, size: Float) {
        super.draw(canvas, paint, center, size)
        val directPoint = getCoordinatesOfCenterRebr(direct, center)
        canvas.drawLine(directPoint.x, directPoint.y, center.x,center.y, paint)
    }

    override fun dead() {
        world.map.addObject(Organic(energy / 2, coordinates, world))
        world.countDeaths++
        super.dead()
    }

    private operator fun compareTo(target: Bio): Int {
        when{
            power * energy > target.power * target.energy -> return 1
            power * energy < target.power * target.energy -> return -1
            power * energy == target.power * target.energy -> return 0
        }

        return 1
    }

    override fun toString(): String {
        return "coordinates: ${coordinates.x}:${coordinates.y}"
    }
}