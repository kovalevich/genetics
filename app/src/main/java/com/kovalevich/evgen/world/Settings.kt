package com.kovalevich.evgen.world

object Settings {
    const val ACTION_ENERGY = 0
    const val MAP_WIDTH = 70
    const val MAP_HEIGHT = 70
    const val MAX_ENERGY = 1000
    const val START_ENERGY = 50
    const val CHILD_ENERGY = 600
    const val STEP_ENERGY = 1
    const val PHOTOSYNTHESIS_ENERGY = 3
    const val SLEEP_TIME = 10
    const val PROBABILITY_MUTATION = 0.06
    const val NUMBER_OF_BEHAVIOR_ITEM = 20
    const val PRINT_SIZE = 10F
    const val PRINT_STROKE = 1F
    const val TIME_LIVE_ORGANIC = 200
    const val SPEED: Long = 100

    // всего кислорода
    const val OXYGEN = 1000000F
    // всего минералов
    const val MINERALS = 1000000F

    const val START_POWER = 1 // статовая сила юнитов
    const val SIZE_BEHAVIOR = 25 // длина поведенческой цепочки днк
    const val COUNT_BEHAVIOR_TYPES = 15 // количество вариантов поведения
    const val SIZE_SKILLS = 5 // количество навыков у сгенерированных юнитов
    const val NEUTRALIZE_TRAP_ENERGY = 20 // энегии требуется для нейтрализации ловушки
    const val NEUTRALIZE_POISON_ENERGY = 20 // энегии требуется для нейтрализации ловушки
}