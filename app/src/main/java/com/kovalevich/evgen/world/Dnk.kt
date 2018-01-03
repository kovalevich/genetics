package com.kovalevich.evgen.world

import java.util.*
import kotlin.collections.ArrayList

/*
 * behavior - поведение юнита
 * последовательность экшенов
 *
 *
 * skills - навыки юнита
 * 0 -> фотосинтез
 * 1 -> еда
 * 2 -> атака
 * 3 -> ловушки
 * 4 -> защита
 * 5 -> яды
 * 6 -> симбиоз
 * 7 -> съесть добычу послеубийства
 * 8 -> деление клетки (рождение потомка)
 *
 * character - характер по 100-бальной шкале 0 - спокойный 100 - агрессивный
 * */
class Dnk(val behavior: List<Int>, private val skills: MutableList<Int>, character: Int = 0) {

    var character = 0
        set(value) {
            field = value
            if(value < 0) field = 0
            if(value > 100) field = 100
        }

    fun addSkill(skill: Int) {
        if(!skills.contains(skill)) skills.add(skill)
    }

    fun hasSkill(skill: Int) = skills.contains(skill)

    fun aggression(): Boolean { // вероятность аггресивного поведения в зависимости от характера

        if((0..100).random() < character) return true

        return false
    }

    /*
    * клониование днк для нового юнита с возможной мутацией 1 гена поведения
    * скилы и хаактер копироются от одителя без изменений*/
    fun clone(): Dnk{
        val b = ArrayList(behavior)
        if(Random().nextFloat() < Settings.PROBABILITY_MUTATION) {
            b[(0..(b.count() - 1)).random()] = (0..Settings.COUNT_BEHAVIOR_TYPES).random()
        }
        return Dnk(b, skills, character)
    }

}