package com.kovalevich.evgen.world

import android.graphics.Point

class Poison(coordinates: Point, world: World): MapObject(coordinates, world) {
    override fun toString(): String {
        return "com.kovalevich.evgen.genetic.Poison map=${coordinates.x}:${coordinates.y}"
    }
}