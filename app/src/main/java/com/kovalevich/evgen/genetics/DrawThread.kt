package com.kovalevich.evgen.genetics

import android.graphics.*
import android.view.SurfaceHolder
import com.kovalevich.evgen.world.Draw
import com.kovalevich.evgen.world.World


/**
 * Created by evgen on 18.12.17.
 */
internal class DrawThread(private val surfaceHolder: SurfaceHolder, val world: World) : Thread() {
    private var runFlag = false
    private var prevTime: Long = 0
    private val paint: Paint = Paint()

    init {
        // сохраняем текущее время
        prevTime = System.currentTimeMillis()
    }

    fun startRun() {
        runFlag = true
    }

    fun stopRun() {
        runFlag = false
    }

    override fun run() {
        var canvas: Canvas? = null
        while (runFlag) {
            // получаем текущее время и вычисляем разницу с предыдущим
            // сохраненным моментом времени
            val now = System.currentTimeMillis()
            if (now - prevTime > 100) {
                // если прошло больше 30 миллисекунд - сохраним текущее время
                prevTime = now
            try {
                // получаем объект Canvas и выполняем отрисовку
                canvas = surfaceHolder.lockCanvas ()
                synchronized(surfaceHolder) {
                    if(canvas != null) {
                        val draw = Draw(canvas, paint, world)
                        draw.draw()
                    }
                }
            } finally {
                if(canvas != null)
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}