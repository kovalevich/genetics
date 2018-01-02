package com.kovalevich.evgen.world

import android.graphics.*

open class MapObject(protected var x: Int, protected var y: Int, private val world: World) {

    /*
    * -1, 0 - мертв
    * 1 - жив
    * 2 - готов родить
    * 3 -
    * 4 -
    * 5 - спит
    * */
    protected var condition = 1
    // возраст объекта в циклах
    open var age = 0

    var drawSize = 0F
    
    // координаты цента для отрисовки
    protected val cx: Float
        get() = getCoordinatesOfCenter().x
    protected val cy: Float
        get() {
            if (x % 2 == 1) {
                return getCoordinatesOfCenter().y + deltaY
            }
            return getCoordinatesOfCenter().y
        }

    var coordinates: Point
        get() = Point(x, y)
        set(value) {
            x = value.x
            y = value.y
        }

    // вспомогательные пееменные
    private val deltaY: Float
        get() = Math.sqrt(Math.pow(drawSize.toDouble(), 2.0) - Math.pow(drawSize.toDouble() / 2, 2.0)).toFloat()

    open val color = Color.GRAY
    open val strokeWidth = Settings.PRINT_STROKE

    // действие объекта за 1 ход
    open fun action(): Boolean{
        age ++
        if (condition == 0) dead()

        //println("----->>>>$this")
        return true
    }

    // убийство объекта на карте
    open protected fun dead() {
        condition = -1
        world.map.deleteObject(coordinates)
    }

    // получить коодинаты центра для отисовки объекта
    private fun getCoordinatesOfCenter(): PointF{
        return PointF(drawSize * 2 * x + drawSize - (drawSize / 2) * x, deltaY * 2 * y + deltaY)
    }

    // получить коодинаты вешин шестиуголька для отрисовки объекта
    protected fun getCoordinatesOfVertices(center: PointF = PointF(cx, cy)): ArrayList<PointF> {

        val i:Float = Math.sqrt(Math.pow(drawSize.toDouble(), 2.0) - Math.pow(drawSize.toDouble() / 2, 2.0)).toFloat()

        return arrayListOf(
                PointF(center.x - drawSize / 2, center.y - i),
                PointF(center.x + drawSize / 2, center.y - i),
                PointF(center.x + drawSize, center.y),
                PointF(center.x + drawSize / 2, center.y + i),
                PointF(center.x - drawSize / 2, center.y + i),
                PointF(center.x - drawSize, center.y)
        )
    }

    // получим координаты центра ребра j
    protected fun getCoordinatesOfCenterRebr(j: Int, center: PointF = PointF(cx, cy)): PointF {
        val i:Float = Math.sqrt(Math.pow(drawSize.toDouble(), 2.0) - Math.pow(drawSize.toDouble() / 2, 2.0)).toFloat()
        val ii:Float = Math.sqrt(Math.pow(i.toDouble(), 2.0) - Math.pow(i.toDouble() / 2, 2.0)).toFloat()

        var coord = PointF(center.x, center.y)
        when(j){
            0 -> coord = PointF(center.x, center.y - i)
            1 -> coord = PointF(center.x + ii, center.y - i / 2)
            2 -> coord = PointF(center.x + ii, center.y + i / 2)
            3 -> coord = PointF(center.x, center.y + i)
            4 -> coord = PointF(center.x - ii, center.y + i / 2)
            5 -> coord = PointF(center.x - ii, center.y - i / 2)
        }
        return coord
    }

    open fun draw(canvas: Canvas, paint: Paint, center: PointF = PointF(cx, cy), size: Float) {

        drawSize = size
        paint.color = color
        paint.strokeWidth = strokeWidth

        val vertices = getCoordinatesOfVertices(center)

        for (j in 0..5) {
            var i = j + 1
            if (j == 5) i = 0
            canvas.drawLine(vertices[j].x, vertices[j].y, vertices[i].x, vertices[i].y, paint)
        }
    }
}