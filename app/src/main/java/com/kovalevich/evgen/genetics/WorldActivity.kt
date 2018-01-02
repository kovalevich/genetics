package com.kovalevich.evgen.genetics

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.WindowManager
import com.kovalevich.evgen.world.World
import org.jetbrains.anko.*

class WorldActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val world = World()
        for (i in 0..100) { // генерируем 100 юнитов для начала жизни
            world.map.generateObject("Bio")
        }

        world.start()
        doAsync { world.run() }
        setContentView(CustomSurfaceView(this, world))
    }
}
