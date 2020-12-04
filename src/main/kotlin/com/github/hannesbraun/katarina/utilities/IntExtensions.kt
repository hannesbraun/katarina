package com.github.hannesbraun.katarina.utilities

fun Int.limit(min: Int, max: Int): Int {
    return if (min > max)
        this
    else if (this < min)
        min
    else if (this > max)
        max
    else
        this
}

fun Int.toBoolean(): Boolean = this != 0
