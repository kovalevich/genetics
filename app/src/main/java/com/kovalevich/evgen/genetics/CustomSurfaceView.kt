package com.kovalevich.evgen.genetics

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.kovalevich.evgen.world.World


class CustomSurfaceView(context: Context, val world: World) : SurfaceView(context), SurfaceHolder.Callback {

    private val drawThread = DrawThread(holder, world)

    init {
        holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawThread.startRun()
        drawThread.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        // завершаем работу потока
        drawThread.stopRun()
        while (retry) {
            try {
                drawThread.join()
                retry = false
            } catch (e: InterruptedException) {
                // если не получилось, то будем пытаться еще и еще
            }

        }
    }
}