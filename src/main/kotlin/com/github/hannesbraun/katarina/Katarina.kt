package com.github.hannesbraun.katarina

import com.github.hannesbraun.katarina.database.*
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.Connection

object KatarinaMeta {
    val version = "4.0.0-SNAPSHOT"
}

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Not enough arguments")
        println("Usage: ${args[0]} <database> <token>")
        return
    }

    val dbFile = args[1]
    val db = Database.connect("jdbc:sqlite:$dbFile", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    initDatabase()

    JDABuilder.createDefault(args[2])
        .addEventListeners(StageOneParser(db))
        .addEventListeners(ReadyListener())
        .build()
}

fun initDatabase() {
    transaction {
        // addLogger(StdOutSqlLogger)
        SchemaUtils.createMissingTablesAndColumns(
            ClassicCommand,
            ClassicCommandRestriction,
            Configuration,
            Gif,
            Joke
        )
    }
}

class ReadyListener : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
        LoggerFactory.getLogger("Supervisor").info("Katarina is alive")
    }
}
