package com.kovalevich.evgen.world

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
 *
 * character - характер по 100-бальной шкале 0 - спокойный 100 - агрессивный
 * */
class Dnk(val behavior: List<Int>, val skills: MutableList<Int>, character: Int = 0) {

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

}