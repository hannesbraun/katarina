package com.github.hannesbraun.katarina

import com.github.hannesbraun.katarina.database.Configuration
import com.github.hannesbraun.katarina.gambling.Gambling
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.modules.admin.Administration
import com.github.hannesbraun.katarina.modules.ccs.ClassicCommandSystem
import com.github.hannesbraun.katarina.modules.danbooru.Danbooru
import com.github.hannesbraun.katarina.modules.joke.Joke
import com.github.hannesbraun.katarina.modules.meta.Meta
import com.github.hannesbraun.katarina.modules.music.MusicBot
import com.github.hannesbraun.katarina.modules.randomanimals.RandomAnimals
import com.github.hannesbraun.katarina.modules.rlc.Rlc
import com.github.hannesbraun.katarina.utilities.KatarinaException
import kotlinx.coroutines.*
import net.dv8tion.jda.api.events.ShutdownEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class StageOneParser(database: Database) : ListenerAdapter() {
    private val stageOneScope = CoroutineScope(Dispatchers.Unconfined)
    private val katarinaConfiguration: KatarinaConfiguration

    init {
        var prefix = "!"
        var botName = "Katarina"
        var ownerId = ""
        var dbVersion = ""
        transaction(database) {
            Configuration.select { Configuration.key eq "cmd_prefix" }.limit(1)
                .forEach { prefix = it[Configuration.value] }
            Configuration.select { Configuration.key eq "bot_name" }.limit(1)
                .forEach { botName = it[Configuration.value] }
            Configuration.select { Configuration.key eq "owner_id" }.limit(1)
                .forEach { ownerId = it[Configuration.value] }
            Configuration.select { Configuration.key eq "db_version" }.limit(1)
                .forEach { dbVersion = it[Configuration.value] }
        }

        if (dbVersion != "1") {
            throw java.lang.RuntimeException("Unsupported database version")
        }

        katarinaConfiguration = KatarinaConfiguration(prefix, ownerId, botName, dbVersion)
    }

    // The classic command system has to be the last entry of the list to avoid covering other commands
    private val modules = listOf<KatarinaModule>(
        Rlc(katarinaConfiguration),
        MusicBot(stageOneScope, katarinaConfiguration),
        Administration(katarinaConfiguration),
        Meta(katarinaConfiguration),
        Danbooru(stageOneScope, katarinaConfiguration),
        RandomAnimals(katarinaConfiguration, stageOneScope),
        Joke(database, katarinaConfiguration),
        Gambling(katarinaConfiguration),
        ClassicCommandSystem(database, katarinaConfiguration)
    )

    override fun onMessageReceived(event: MessageReceivedEvent) {
        stageOneScope.launch {
            if (event.author.isBot)
                return@launch

            if (!event.message.contentRaw.trim().startsWith(katarinaConfiguration.prefix))
                return@launch

            for (module in modules) {
                if (module !is MessageReceivedHandler)
                    continue

                val handled = try {
                    module.tryHandleMessageReceived(event)
                } catch (e: KatarinaException) {
                    event.channel.sendMessage(e.localizedMessage)
                        .delay(24, TimeUnit.HOURS)
                        .flatMap { it.delete() }
                        .queue()

                    LoggerFactory.getLogger("StageOneParser").info("\"${event.message.contentRaw}\" : ${e.message}")
                    true
                } catch (e: RuntimeException) {
                    LoggerFactory.getLogger("StageOneParser").error("${e.javaClass.name}: ${e.message}")
                    true
                }

                if (handled)
                    return@launch
            }
        }
    }

    override fun onShutdown(event: ShutdownEvent) {
        stageOneScope.cancel()
    }

    fun finalize() {
        stageOneScope.cancel()
    }
}

data class KatarinaConfiguration(
    val prefix: String,
    val ownerId: String,
    val botName: String,
    val dbVersion: String
)
