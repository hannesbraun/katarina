package com.github.hannesbraun.katarina

import com.github.hannesbraun.katarina.modules.rlc.RlcParser
import net.dv8tion.jda.api.JDABuilder
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Please specify a token for the bot")
        return
    }

    val dbFile = args[1]
    Database.connect("jdbc:sqlite:$dbFile")
    // SchemaUtils.create(Configuration)

    val jda = JDABuilder.createDefault(args[2]).build()
    jda.addEventListener(RlcParser())
}
