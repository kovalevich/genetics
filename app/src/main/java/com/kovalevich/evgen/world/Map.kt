package com.kovalevich.evgen.world

import android.graphics.Point
import java.util.*

class Map(val world: World){

    private val objects: MutableList<MapObject> = mutableListOf()
    private val lockObjects: List<MapObject>
        get() = ArrayList(objects)
    val bios: List<Bio>
        get() {
            return lockObjects.filter { it is Bio } as List<Bio>
        }
    val organics: List<Organic>
        get() {
            return lockObjects.filter { it is Organic } as List<Organic>
        }

    val traps: List<Trap>
        get() {
            return lockObjects.filter { it is Trap } as List<Trap>
        }

    fun addObject(obj: MapObject){
        objects.add(obj)
    }

    fun deleteObject(coordinates: Point){
        val obj = objects.find { it.coordinates == coordinates }
        if (obj != null)
            objects.remove(obj)
    }

    fun getDirectObject(direct: Int, coordinates: Point): MapObject? {

        val directCoordinates = getDirectCoordinates(direct, coordinates)

        if (directCoordinates != null) {
            val obj = objects.find { it.coordinates == directCoordinates }

            if (obj != null) return obj

            return Empty(directCoordinates.x, directCoordinates.y, world)
        }

        return null
    }

    private fun getDirectCoordinates(direct: Int, coordinates: Point): Point? {

        var x = coordinates.x
        var y = coordinates.y

        when (direct) {
            0 -> { // шаг вперед
                if (y > 0) y--
            }
            1 -> { // идем вверх-право
                if(x + 1 < Settings.MAP_WIDTH && y > 0) {
                    x++
                    y--
                }
            }
            2 -> { // идем низ-право
                if(x + 1 < Settings.MAP_WIDTH && y + 1 < Settings.MAP_HEIGHT) {
                    y++
                    x++
                }
            }
            3 -> { // идем вниз
                if (y + 1 < Settings.MAP_HEIGHT)
                    y++
            }
            4 -> { // идем вниз-лево
                if(x > 0 && y + 1 < Settings.MAP_HEIGHT) {
                    y++
                    x--
                }
            }
            5 -> { // идем вверх-лево
                if(x > 0 && y > 0) {
                    x--
                    y--
                }
            }
            else -> {
                return null
            }
        }

        if (x == coordinates.x && y == coordinates.y) return null

        return Point(x, y)
    }

    fun getAroundObjects(coordinates: Point): MutableList<Any?> {
        val objectList = mutableListOf<Any?>()
        (0..5).toList().map{ direct -> objectList.add(getDirectObject(direct, coordinates))}

        return objectList
    }

    fun generateObject (type: String) {
        when(type) {
            "Bio" -> generateBio(world)
        }
    }

    private fun generateBio(world: World) {
        val baseDnk = listOf(0,1,2,3,4,5,6,8,10,11,15)
        addObject(Bio(
                List(Settings.NUMBER_OF_GENES, {baseDnk[Random().nextInt(baseDnk.size)]}),
                (0 until Settings.MAP_WIDTH).random(), (0 until Settings.MAP_HEIGHT).random(),
                world
        ))
    }

    override fun toString(): String {
        var str = ""

        objects.forEach {
            str += "$it\n"
        }
        return str
    }

    fun findPartnerObject(coordinates: Point, sex: Boolean): Bio? {

        val partner = bios.find { it.sex != sex && coordinates.x in ((it.coordinates.x - 5)..(it.coordinates.x + 5)) && coordinates.y in ((it.coordinates.y - 5)..(it.coordinates.y + 5)) }
        if(partner != null) return partner

        return null
    }

    fun getDirectToObject(from: Bio, to: MapObject): Int {
        when {
            from.coordinates.x == to.coordinates.x && from.coordinates.y > to.coordinates.y -> { // шаг вперед
                return 0
            }
            from.coordinates.x < to.coordinates.x && from.coordinates.y > to.coordinates.y -> { // идем вверх-право
                return 1
            }
            from.coordinates.x < to.coordinates.x && from.coordinates.y == to.coordinates.y -> { // идем низ-право
                return 2
            }
            from.coordinates.x < to.coordinates.x && from.coordinates.y < to.coordinates.y -> { // идем вниз
                return 2
            }
            from.coordinates.x == to.coordinates.x && from.coordinates.y < to.coordinates.y -> return 3
            from.coordinates.x > to.coordinates.x && from.coordinates.y < to.coordinates.y -> return 4
            from.coordinates.x > to.coordinates.x && from.coordinates.y == to.coordinates.y -> return 4
            from.coordinates.x > to.coordinates.x && from.coordinates.y > to.coordinates.y -> return 5
            else -> {
                return from.direct
            }
        }
    }
}