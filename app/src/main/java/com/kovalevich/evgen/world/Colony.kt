package com.kovalevich.evgen.world

import android.graphics.Color

/* класс колонии описывает интерфейс создания колоний юнитов
* и взаимодействие юнито в колонии */

class Colony(val world: World) {

    // список жителей колонии
    private val population = mutableListOf<Bio>()

    /* цвет колонии*/
    val color = Color.RED + (1000..5000).random()

    val count: Int
        get() = population.count()

    // добавление жителя в колонию
    fun addBio(bio: Bio) = population.add(bio)
    // уходжителя из колонии
    fun deleteBio(bio: Bio) = population.remove(bio)

    /* проверить близко ли объект к колонии
    * если близко true*/
    fun checkDistance(bio: MapObject): Boolean{
        if(population.find {
            bio.coordinates.x in ((it.coordinates.x - 2)..(it.coordinates.x + 2)) &&
                    bio.coordinates.y in ((it.coordinates.y - 2)..(it.coordinates.y + 2)) &&
                    bio != it
        } == null) return false

        return true
    }
}