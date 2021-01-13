package com.github.hannesbraun.katarina

import com.github.hannesbraun.katarina.database.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.Connection

object KatarinaMeta {
    const val version = "4.1.1-SNAPSHOT"
}

enum class RunMode {
    RUN,
    INSERT_DEFAULT_GIFS;

    override fun toString() = when (this) {
        RUN -> "run"
        INSERT_DEFAULT_GIFS -> "initGifs"
    }
}

fun main(args: Array<String>) {
    // Parse arguments
    val parser = ArgParser("katarina")
    val databaseFile by parser.option(ArgType.String, shortName = "d", description = "SQLite database file").required()
    val token by parser.option(ArgType.String, shortName = "t", description = "Bot token")
    val runMode by parser.option(ArgType.Choice<RunMode>(), shortName = "m", description = "Run mode")
        .default(RunMode.RUN)
    parser.parse(args)

    val db = Database.connect("jdbc:sqlite:$databaseFile", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    initDatabase(db)
    initConfiguration(db)

    when (runMode) {
        RunMode.RUN -> JDABuilder.createDefault(token)
            .addEventListeners(StageOneParser(db))
            .addEventListeners(ReadyListener())
            .build()
        RunMode.INSERT_DEFAULT_GIFS -> insertDefaultGifs(db)
    }
}

fun initDatabase(database: Database) {
    transaction(database) {
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
