package com.kovalevich.evgen.world

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import java.util.*

class Bio(private val dnk: Dnk, coordinates: Point, val world: World): MapObject(coordinates,world) {

    /*
    текущее направление инициализируется рандомно
    0 - вверх
    1 - верх-право
    2 - низ-право
    3 - вниз
    4 - низ-лево
    5 - верх-лево
     */
    var direct = (0..5).random()
        set(value) {
            field = value
            if (value < 0) field = 5
            if (value > 5) field = 0
        }

    /* пол юнита задается рандомно при инициализации объекта
    true = муж.
    false = жен.
     */
    val sex = Random().nextBoolean()

    /*
    индикатор следующего action
    инкриментируется при каждом ходе
     */
    private var currentAction = 0
        set(value) {
            field = value
            if (value < 0) field = Settings.NUMBER_OF_BEHAVIOR_ITEM
            if (value > Settings.NUMBER_OF_BEHAVIOR_ITEM) field = 0
        }

    override fun action(): Boolean {

        /* в зависимости от текущего номера экшена выполняем действие из набора
        все действия по умолчанию выполняются по направлению юнита direct
         */
        when(currentAction++) {
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
            else -> return false
        }

        return super.action()
    }

    private fun neutralizeTrap(trap: MapObject? = world.map.getDirectObject(direct, coordinates)): Boolean { // нейтрализовать ловушку
        if (trap !is Trap) return false

        if (trap.visible && dnk.skills.contains(3)) { // если ловушку видно и есть навык работы с ловушками, то обезвреживаем
            coordinates = trap.coordinates
            world.map.deleteObject(trap.coordinates)

            // окружающие юниты получают навык

        }
        else { // иначе умираем
            trap.visible = true
            dead()
        }

        return true
    }

    private fun setTrap() { // поставить ловушку
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun eat() { // кушать
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun symbiosis() { // симбиоз
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun explore() { // исследовать окресность
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun synthesisOfMinerals() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun photosynthesis() { // фотосинтез
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun attack() { // атаковать
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun step() { // шаг вперед
        // проверяем что у нас по направлению движения
        val directObj = world.map.getDirectObject(direct, coordinates)

        when(directObj){
            is Bio -> { // если по направлению движения находится другой юнит действуем по обстоятельствам

            }
            is Trap -> { // если ловушка обнаружена ранее, то пробуем обезвредить, если нет, попадаем в ловушку
                neutralizeTrap()
            }
            is Empty -> coordinates = directObj.coordinates
            is Poison -> {}
        }
    }

    private fun right() { // повернуться направо
        direct ++
    }

    private fun left() { // повернуться налево
        direct --
    }

    override fun draw(canvas: Canvas, paint: Paint, center: PointF, size: Float) {
        super.draw(canvas, paint, center, size)
    }

    override fun dead() {
        world.map.addObject(Organic(coordinates.x, coordinates.y, world))
        super.dead()
    }
}