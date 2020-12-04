package com.github.hannesbraun.katarina.modules.randomanimals

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.KatarinaMeta
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.google.gson.JsonParser
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.LoggerFactory

class RandomAnimals(config: KatarinaConfiguration, private val scope: CoroutineScope) : KatarinaModule(),
    MessageReceivedHandler {
    private val parser = RandomAnimalsParser(config)

    private val catChannel = Channel<String>(1)
    private val dogChannel = Channel<String>(1)

    init {
        scope.launch { catProducer() }
        scope.launch { dogProducer() }
    }

    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        val animal = parser.parse(event.message.contentRaw) ?: return false

        scope.launch {
            val url = when (animal) {
                Animal.CAT -> catChannel.receive()
                Animal.DOG -> dogChannel.receive()
            }

            event.channel.sendMessage(
                EmbedBuilder()
                    .setTitle(
                        when (animal) {
                            Animal.CAT -> "Cat!"
                            Animal.DOG -> "Dog!"
                        }
                    )
                    .setColor(
                        when (animal) {
                            Animal.CAT -> 0xee44a3
                            Animal.DOG -> 0x44eeb2
                        }
                    )
                    .setImage(url)
                    .build()
            ).queue()
        }

        return true
    }

    private suspend fun catProducer(): Nothing {
        while (true) {
            try {
                val jsonString = HttpClient(CIO).use { client ->
                    client.get<String>("https://aws.random.cat/meow") {
                        header(
                            "User-Agent",
                            "Katarina ${KatarinaMeta.version}"
                        )
                    }
                }
                catChannel.send(JsonParser.parseString(jsonString).asJsonObject.get("file").asString)
            } catch (e: Exception) {
                LoggerFactory.getLogger("CatProducer").error(e.message)
                delay(3600000)
                continue
            }
        }
    }

    private suspend fun dogProducer(): Nothing {
        while (true) {
            try {
                val jsonString = HttpClient(CIO).use { client ->
                    client.get<String>("https://random.dog/woof.json") {
                        header(
                            "User-Agent",
                            "Katarina ${KatarinaMeta.version}"
                        )
                    }
                }
                dogChannel.send(JsonParser.parseString(jsonString).asJsonObject.get("url").asString)
            } catch (e: Exception) {
                LoggerFactory.getLogger("DogProducer").error(e.message)
                delay(3600000)
                continue
            }
        }
    }
}