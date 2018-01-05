package com.kovalevich.evgen.world

import android.graphics.*

class Trap(coordinates: Point, world: World): MapObject(coordinates, world) {

    var visible: Boolean = false

    override val color: Int
        get() {
            if (visible) return Color.DKGRAY
            return Color.GRAY
        }

    override fun toString(): String {
        return "com.kovalevich.evgen.genetic.Trap map=${coordinates.x}:${coordinates.y}"
    }

    override fun draw(canvas: Canvas, paint: Paint, center: PointF, size: Float) {
        super.draw(canvas, paint, center, size)

        val xy = getCoordinatesOfVertices(center)
        xy.forEach {
            canvas.drawLine(it.x, it.y, center.x, center.y, paint)
        }
    }
}