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
 * 6 -> */
class Dnk(val behavior: List<Int>, val skills: MutableList<Int>) {

    fun addSkill(skill: Int) {
        if(!skills.contains(skill)) skills.add(skill)
    }

    fun hasSkill(skill: Int) = skills.contains(skill)

}