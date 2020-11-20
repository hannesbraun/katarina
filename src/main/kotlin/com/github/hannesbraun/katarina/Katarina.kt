package com.github.hannesbraun.katarina

import net.dv8tion.jda.api.JDABuilder
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Not enough arguments")
        println("Usage: ${args[1]} <database> <token>")
        return
    }

    val dbFile = args[1]
    Database.connect("jdbc:sqlite:$dbFile")
    // SchemaUtils.create(Configuration)

    val jda = JDABuilder.createDefault(args[2])
            .addEventListeners(StageOneParser())
            .build()
}
