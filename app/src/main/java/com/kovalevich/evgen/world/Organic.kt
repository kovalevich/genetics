package com.kovalevich.evgen.world

import android.graphics.Point

class Organic(val energy: Int, coordinates: Point, world: World): MapObject(coordinates, world) {

    override var age = 0
        set (value) {
            if (age > Settings.TIME_LIVE_ORGANIC) {
                condition = 0
            }
            field = value
        }

    override fun toString(): String {
        return "com.kovalevich.evgen.genetic.Organic map=${coordinates.x}:${coordinates.y}"
    }
}