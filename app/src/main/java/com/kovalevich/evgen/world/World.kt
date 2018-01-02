package com.kovalevich.evgen.world

import java.util.*

fun ClosedRange<Int>.random() =
        Random().nextInt(endInclusive - start + 1) +  start

class World {

    // содержание углекислого газа
    val carbonDioxide
        get() = Settings.OXYGEN - oxygen

    // содержание кислорода
    var oxygen = Settings.OXYGEN
        set(value) {
            field = 0F
            when{
                value > 0 && value <= Settings.OXYGEN -> field = value
                value > Settings.OXYGEN -> field = Settings.OXYGEN
            }
        }

    // содержание минералов
    var minerals = Settings.MINERALS
        set(value) {
            field = 0F
            when{
                value > 0 && value < Settings.MINERALS -> field = value
                value > Settings.MINERALS -> field = Settings.MINERALS
            }
        }

    val map = Map(this)
    var countChildrens = 0
    var countDeaths = 0
    private var runFlag = false
    var countKills = 0

    fun run () {
        while (runFlag) {
            Thread.sleep(1000 / Settings.SPEED)
            map.bios.forEach {
                it.action()
            }
            map.organics.forEach {
                it.action()
            }
        }
    }

    fun start (){
        runFlag = true
    }

    fun stop (){
        runFlag = false
    }

    override fun toString(): String {
        return "com.kovalevich.evgen.genetic.World: kills:$countKills childrens=$countChildrens deds=$countDeaths"
    }
}