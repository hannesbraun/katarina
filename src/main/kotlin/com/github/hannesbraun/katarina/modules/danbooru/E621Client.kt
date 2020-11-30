package com.github.hannesbraun.katarina.modules.danbooru

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random

class E621Client(scope: CoroutineScope) : DanbooruClient(scope) {
    override val name = "e621"
    override val indexUrl = "https://e621.net/posts.json"

    override fun getIndexParameters(): List<Pair<String, String>> {
        val page = Random.nextInt(1, 751)
        return listOf(Pair("limit", postBufferSize.toString()), Pair("page", page.toString()))
    }

    override fun generatePost(
        id: String, fileUrl: String, rating: String, score: String, tagList: List<String>
    ) = E621Post(id, fileUrl, rating, score, tagList)

    override fun getIndexList(response: JsonElement): JsonArray = response.asJsonObject.get("posts").asJsonArray
    override fun getFileUrlFromJson(post: JsonObject): String = post.get("file").asJsonObject.getString("url")
    override fun getScoreFromJson(post: JsonObject): String = post.get("score").asJsonObject.getString("total")
    override fun getTagListFromJson(post: JsonObject): List<String> {
        val tags = post.get("tags").asJsonObject.entrySet().map { category ->
            category.value.asJsonArray.map { tag -> tag.asString }
        }
        return tags.flatten()
    }
}

class E621Post(id: String, fileUrl: String, rating: String, score: String, tagList: List<String>) : DanbooruPost(
    id,
    fileUrl,
    rating,
    score,
    tagList
) {
    override fun getFullPostUrl() = "https://e621.net/posts/$id"
}
