package com.gmail.rager1958.playnearbyapi

import kotlin.random.Random

class CodeNameGenerator private constructor() {
    companion object {
        private val RNG = Random(System.currentTimeMillis())
        private val fruits = arrayOf("香蕉", "西瓜")
        private val animals = arrayOf("貓咪", "狗狗")
        fun genCodeName() =
            fruits[RNG.nextInt(fruits.size)] + " " + animals[RNG.nextInt(animals.size)] + RNG.nextInt(
                100
            )
    }
}