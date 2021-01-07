package com.github.hannesbraun.katarina.modules.danbooru

import com.github.hannesbraun.katarina.KatarinaMeta
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import org.slf4j.LoggerFactory
import java.lang.System.currentTimeMillis
import kotlin.random.Random

open class DanbooruClient(private val scope: CoroutineScope) {
    /* The size of the channel */
    protected val postBufferSize = 100
    private val maxRetries = 96

    private val postChannel = Channel<DanbooruPost>(postBufferSize)

    fun init() {
        scope.launch { providePosts(postChannel) }
    }

    protected open val name = "Danbooru"
    protected open val indexUrl = "https://danbooru.donmai.us/posts.json"
    private val errorExplicitEmbed = EmbedBuilder()
        .setTitle("Error")
        .setDescription("No explicit post found.")
        .setColor(0xff001f)
        .build()

    suspend fun getPostAsEmbed(explicitOnly: Boolean): MessageEmbed {
        val deferredPost = scope.async { postChannel.receive() }

        val embedBuilder = EmbedBuilder()
            .setTitle(name)
            .setColor(0xd480ff)

        var post = deferredPost.await()
        var tries = 0
        while (tries < maxRetries && !post.isUsable(explicitOnly)) {
            post = postChannel.receive()
            tries++
        }

        if (tries == maxRetries && !post.isUsable(explicitOnly))
            return errorExplicitEmbed

        return embedBuilder
            .setImage(post.fileUrl)
            .addField("\ud83d\uddd2 Tags", post.getFormattedTags(), true)
            .addField("\ud83d\udcc8 Score", post.score, true)
            .addField("\ud83d\udcce Full post", "[Click here](${post.getFullPostUrl()})", true)
            .build()
    }

    protected open fun getIndexParameters(): List<Pair<String, String>> {
        val page = Random.nextInt(1, 1001)
        return listOf(Pair("limit", postBufferSize.toString()), Pair("page", page.toString()))
    }

    private suspend fun providePosts(channel: SendChannel<DanbooruPost>): Nothing {
        while (true) {
            // As soon as the channel is full the client will already request postBufferSize new posts
            // So sometimes, there can effectively be postBufferSize posts ready to use
            val response = try {
                HttpClient(CIO).use { client ->
                    client.get<String>(indexUrl) {
                        header("User-Agent", "Katarina ${KatarinaMeta.version}")
                        getIndexParameters().forEach { parameter(it.first, it.second) }
                    }
                }
            } catch (e: Exception) {
                // Catching everything seems hacky... but this method should not terminate
                // If it would, the client would not work anymore as soon as it terminates
                // So it's probably fine
                LoggerFactory.getLogger("DanbooruClient").error(e.message)
                delay(3600000)
                continue
            }

            // Parse JSON
            val index = try {
                getIndexList(JsonParser.parseString(response))
            } catch (e: Exception) {
                LoggerFactory.getLogger("DanbooruClient").error("Unable to get index list")
                delay(7200000)
                continue
            }
            val posts: List<DanbooruPost> = index.filter { it.isJsonObject }.mapNotNull {
                return@mapNotNull try {
                    val obj = it.asJsonObject
                    generatePost(
                        getIdFromJson(obj),
                        getFileUrlFromJson(obj),
                        getRatingFromJson(obj),
                        getScoreFromJson(obj),
                        getTagListFromJson(obj)
                    )
                } catch (e: Exception) {
                    DanbooruPost("", "", "", "", emptyList())
                }
            }.shuffled()

            posts.forEach {
                if (it.fileUrl.isNotBlank())
                    channel.send(it)
            }
        }
    }

    protected open fun generatePost(
        id: String, fileUrl: String, rating: String, score: String, tagList: List<String>
    ) = DanbooruPost(id, fileUrl, rating, score, tagList)

    protected open fun getIdFromJson(post: JsonObject): String = post.getString("id")
    protected open fun getFileUrlFromJson(post: JsonObject): String = post.getString("file_url")
    protected open fun getRatingFromJson(post: JsonObject): String = post.getString("rating")
    protected open fun getScoreFromJson(post: JsonObject): String = post.getString("score")
    protected open fun getTagListFromJson(post: JsonObject): List<String> = post.getString("tag_string").split(" ")

    protected open fun getIndexList(response: JsonElement): JsonArray = response.asJsonArray

    fun finalize() {
        scope.cancel()
    }
}

open class DanbooruPost(
    val id: String,
    val fileUrl: String,
    val rating: String,
    score: String,
    val tagList: List<String>
) {
    val score = score
        get() {
            return if (field.isBlank())
                "*No score available*"
            else
                field
        }

    private val received = currentTimeMillis()
    private val outdatedMilliseconds = 2 * 60 * 1000
    private fun isOutdated(): Boolean = (received - currentTimeMillis()) > outdatedMilliseconds

    protected open fun isExplicit(): Boolean = rating == "e"
    open fun getFullPostUrl() = "https://danbooru.donmai.us/posts/$id"

    fun isUsable(explicitOnly: Boolean): Boolean = !isOutdated()
            && (!explicitOnly || isExplicit())

    fun getFormattedTags(): String {
        if (tagList.isNotEmpty()) {
            var formattedTags: String
            var amount = tagList.size
            do {
                formattedTags = tagList.joinToString("`, `", "`", "`", amount)
                amount--
                if (amount <= 0) {
                    return "*A tag exceeds the max. field size of Discord embeds.*"
                }
            } while (formattedTags.length >= 1024)
            return formattedTags
        } else {
            return "*No tags available*"
        }
    }
}

fun JsonObject.getString(memberName: String): String {
    val element = this.get(memberName)
    return if (element == null)
        ""
    else
        element.asString
}
