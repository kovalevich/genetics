package com.kovalevich.evgen.world

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Draw(private val canvas: Canvas, private val paint: Paint, val world: World) {

    private val drawSize: Float
        get() = canvas.width.toFloat() / (1.5F * Settings.MAP_WIDTH)

    private var i = 1000

    private fun drawObjects(){
        canvas.drawColor(Color.WHITE)

        world.map.organics.forEach {
            it.draw(canvas, paint, size = drawSize)
        }
        world.map.traps.forEach {
            it.draw(canvas, paint, size = drawSize)
        }
        world.map.bios.forEach {
            it.draw(canvas, paint, size = drawSize)
        }
    }

    private fun drawInfo(){
        paint.textSize = 20F
        paint.color = Color.DKGRAY
        paint.textAlign = Paint.Align.LEFT

        val bottomY = canvas.height.toFloat() - 20

        var lifeTime = 0
        if (world.map.bios.count() > 0)
            lifeTime = world.map.bios.sumBy { it.age } / world.map.bios.count()
        val text = "живых: ${world.map.bios.count()}" +
                " родилось: ${world.countChildrens}" +
                " умерло: ${world.countDeaths}" +
                " средний возраст: $lifeTime"

        canvas.drawText(text, 10F,bottomY, paint)

        val text1 = "кислорода: ${(world.oxygen / Settings.OXYGEN)*100}%"
        canvas.drawText(text1, 10F,bottomY - 25, paint)
    }


    private fun drawControlls(){

    }

    fun draw() {
        drawObjects()
        drawInfo()
        //drawDnk()
    }
}