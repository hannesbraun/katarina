package com.github.hannesbraun.katarina.utilities

fun Int.limit(min: Int, max: Int) = when {
    min > max -> this
    this < min -> min
    this > max -> max
    else -> this
}

fun Int.toBoolean(): Boolean = this != 0
