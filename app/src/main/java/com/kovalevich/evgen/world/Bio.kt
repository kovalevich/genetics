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
        get() = Color.RED + power * 10

    override fun action(): Boolean {

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
            else -> return false
        }

        return super.action()
    }

    /* деление юнита, если хватает энергии */
    private fun division(): Boolean {
        if(energy <= Settings.CHILD_ENERGY) return false

        val emptyObj = world.map.getAroundObjects(coordinates).find { it is Empty }
        if (emptyObj is Empty) {
            world.map.addObject(Bio(dnk.clone(), emptyObj.coordinates, world))
            energy -= Settings.CHILD_ENERGY
        }
        else return false
        return true
    }

    private fun neutralizeTrap(trap: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean { // нейтрализовать ловушку
        if (trap !is Trap) return false

        if (trap.visible && dnk.hasSkill(3)) { // если ловушку видно и есть навык работы с ловушками, то обезвреживаем
            coordinates = trap.coordinates
            world.map.deleteObject(trap.coordinates)
            energy -= Settings.NEUTRALIZE_TRAP_ENERGY
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

        world.map.addObject(Trap(directObject.coordinates, world))
        teachOthers(3)

        return true
    }

    private fun eat(target: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean { // кушать
        if(!dnk.hasSkill(1)) return false

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

    private fun symbiosis(target: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean { // симбиоз
        if(target !is Bio || !target.dnk.hasSkill(6) || !dnk.hasSkill(6)) return false


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
        if(!dnk.hasSkill(2)) return false

        when(target) {
            is Bio -> {
                direct = getDirectToObject(coordinates, target.coordinates)
                if(this == target) {
                    dead()
                    target.dead()
                    world.countKills += 2
                    return true
                }

                target.dnk.character++ // увеличение агрессии цели

                if (target > this) {
                    target.attack(this)
                    return false
                }

                /*
                * расходуем энергию на убийство
                * увеличиваем мощность юнита
                * увеличиваем счетчик убийств
                * умирает цель
                * если есть навык, кушаем добычу и учим другихтак делать*/
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

    private fun step(directObj: MapObject? = world.map.getDirectObject(direct, coordinates)) { // шаг вперед

        when(directObj){
            is Bio -> { // если по направлению движения находится другой юнит действуем по характеру
                if(dnk.aggression()) attack(directObj)
            }
            is Trap -> dead()
            is Empty -> coordinates = directObj.coordinates
            is Poison -> dead()
        }
        energy -= Settings.STEP_ENERGY
    }

    private fun right() { // повернуться направо
        direct++
        energy--
    }

    private fun left() { // повернуться налево
        direct--
        energy--
    }

    private fun teachOthers(skill: Int) { // обучение окружающих юнитов
        val aroundObjects = world.map.getAroundObjects(coordinates).filter { it is Bio }
        aroundObjects.forEach { (it as Bio).dnk.addSkill(skill) }
    }

    override fun draw(canvas: Canvas, paint: Paint, center: PointF, size: Float) {
        super.draw(canvas, paint, center, size)
        val directPoint = getCoordinatesOfCenterRebr(direct)
        canvas.drawLine(directPoint.x, directPoint.y, cx, cy, paint)
    }

    override fun dead() {
        world.map.addObject(Organic(energy / 2, coordinates, world))
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