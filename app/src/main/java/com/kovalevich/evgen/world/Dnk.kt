package com.kovalevich.evgen.world

/*
 * behavior - поведение юнита
 * последовательность экшенов
 *
 *
 * skills - навыки юнита
 * 0 -> фотосинтез
 * 1 -> еда
 * 2 -> убийство
 * 3 -> ловушки
 * 4 -> защита
 * 5 -> яды
 * 6 -> */
class Dnk(val behavior: List<Int>, val skills: MutableList<Int>)