package com.kovalevich.evgen.genetics

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val worldIntent = Intent(this, WorldActivity::class.java)
        verticalLayout {
            button {
                text = "Start"
                onClick {
                    startActivity(worldIntent)
                }
            }.lparams(width = matchParent) {
                horizontalMargin = dip(0)
                bottomMargin = dip(0)
                gravity = bottom
            }
            padding = 0
        }

    }
}
