package com.github.hannesbraun.katarina.utilities

fun String.limit(max : Int) : String {
    return if (this.length > max)
        this.substring(0, max)
    else
        this
}

fun String.limitWithDots(max : Int) : String {
    return if (this.length > max) {
        this.substring(0, max - 3) + "..."
    } else {
        this
    }
}

fun String.removeRedundantSpaces() = this.replace(Regex(" +"), " ")
