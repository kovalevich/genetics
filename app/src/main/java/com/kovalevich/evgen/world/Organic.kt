package com.kovalevich.evgen.world

class Organic(x: Int, y: Int, world: World): MapObject(x,y, world) {

    override var age = 0
        set (value) {
            if (age > Settings.TIME_LIVE_ORGANIC) {
                condition = 0
            }
            field = value
        }

    override fun toString(): String {
        return "com.kovalevich.evgen.genetic.Organic map=${x}:${y}"
    }
}