package com.kovalevich.evgen.world

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF

class Trap(x: Int, y: Int, world: World): MapObject(x, y, world) {

    var visible: Boolean = false

    override val color: Int
        get() {
            if (visible) return Color.DKGRAY
            return Color.GRAY
        }

    override fun toString(): String {
        return "com.kovalevich.evgen.genetic.Trap map=$x:$y"
    }

    override fun draw(canvas: Canvas, paint: Paint, center: PointF, size: Float) {
        super.draw(canvas, paint, center, size)

        val xy = getCoordinatesOfVertices()
        xy.forEach {
            canvas.drawLine(it.x, it.y, cx, cy, paint)
        }
    }
}