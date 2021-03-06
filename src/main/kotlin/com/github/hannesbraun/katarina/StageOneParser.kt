package com.github.hannesbraun.katarina

import com.github.hannesbraun.katarina.database.Configuration
import com.github.hannesbraun.katarina.database.ConfigurationConstants
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.modules.admin.Administration
import com.github.hannesbraun.katarina.modules.ccs.ClassicCommandSystem
import com.github.hannesbraun.katarina.modules.danbooru.Danbooru
import com.github.hannesbraun.katarina.modules.gambling.Gambling
import com.github.hannesbraun.katarina.modules.gif.GifModule
import com.github.hannesbraun.katarina.modules.joke.Joke
import com.github.hannesbraun.katarina.modules.math.Math
import com.github.hannesbraun.katarina.modules.meta.Meta
import com.github.hannesbraun.katarina.modules.music.MusicBot
import com.github.hannesbraun.katarina.modules.randomanimals.RandomAnimals
import com.github.hannesbraun.katarina.modules.rlc.Rlc
import com.github.hannesbraun.katarina.utilities.KatarinaException
import com.github.hannesbraun.katarina.utilities.deleteAfter
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
        var prefix = ConfigurationConstants.defaultPrefix
        var botName = ConfigurationConstants.defaultBotName
        var ownerId = ConfigurationConstants.defaultOwnerId
        var dbVersion = "" // Not using default value as fallback since the correct version string has to be present
        transaction(database) {
            Configuration.select { Configuration.key eq ConfigurationConstants.keyPrefix }.limit(1)
                .forEach { prefix = it[Configuration.value] }
            Configuration.select { Configuration.key eq ConfigurationConstants.keyBotName }.limit(1)
                .forEach { botName = it[Configuration.value] }
            Configuration.select { Configuration.key eq ConfigurationConstants.keyOwnerId }.limit(1)
                .forEach { ownerId = it[Configuration.value] }
            Configuration.select { Configuration.key eq ConfigurationConstants.keyDbVersion }.limit(1)
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
        GifModule(katarinaConfiguration),
        RandomAnimals(katarinaConfiguration, stageOneScope),
        Math(katarinaConfiguration),
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
                        .deleteAfter(24, TimeUnit.HOURS)
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
