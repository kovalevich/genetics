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

    private fun drawDnk()
    {
        paint.textSize = 24F
        paint.textAlign = Paint.Align.RIGHT
        val color = Color.RED

        val x = canvas.width.toFloat() - 10

        var count = world.map.bios.filter { it.dnk.contains(0) || it.dnk.contains(1) }.count()
        paint.color = color + 100*count
        canvas.drawText("$count повороты", x,20F, paint)
        count = world.map.bios.filter { it.dnk.contains(2)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count шаг", x,40F, paint)
        count = world.map.bios.filter { it.dnk.contains(3)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count исследование", x,60F, paint)
        count = world.map.bios.filter { it.dnk.contains(4)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count фотосинтез", x,80F, paint)
        count = world.map.bios.filter { it.dnk.contains(5)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count сон", x,100F, paint)
        count = world.map.bios.filter { it.dnk.contains(6)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count еда", x,120F, paint)
        count = world.map.bios.filter { it.dnk.contains(7)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count охота", x,140F, paint)
        count = world.map.bios.filter { it.dnk.contains(8)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count умное убийство", x,160F, paint)
        count = world.map.bios.filter { it.dnk.contains(9)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count убийство", x,180F, paint)
        count = world.map.bios.filter { it.dnk.contains(10)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count симбиоз", x,200F, paint)
        count = world.map.bios.filter { it.dnk.contains(11)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count найти выход", x,220F, paint)
        count = world.map.bios.filter { it.dnk.contains(12)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count обезвредить ловушку", x,240F, paint)
        count = world.map.bios.filter { it.dnk.contains(13)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count поставить ловушку", x,260F, paint)
        count = world.map.bios.filter { it.dnk.contains(14)}.count()
        paint.color = color + 100*count
        canvas.drawText("$count внезапная смерть", x,280F, paint)

    }

    private fun drawControlls(){

    }

    fun draw() {
        drawObjects()
        drawInfo()
        //drawDnk()
    }
}