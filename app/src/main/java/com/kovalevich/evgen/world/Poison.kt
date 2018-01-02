package com.kovalevich.evgen.world

class Poison(x: Int, y: Int, world: World): MapObject(x, y, world) {
    override fun toString(): String {
        return "com.kovalevich.evgen.genetic.Poison map=${x}:${y}"
    }
}