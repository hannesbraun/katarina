package com.github.hannesbraun.katarina.modules.danbooru

import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random

class KonachanClient(scope: CoroutineScope) : DanbooruClient(scope) {
    override val name = "Konachan"
    override val indexUrl = "https://konachan.com/post.json"

    override fun getIndexParameters(): List<Pair<String, String>> {
        val page = Random.nextInt(1, 2550)
        return listOf(Pair("limit", postBufferSize.toString()), Pair("page", page.toString()))
    }

    override fun generatePost(
        id: String, fileUrl: String, rating: String, score: String, tagList: List<String>
    ) = KonachanPost(id, fileUrl, rating, score, tagList)

    override fun getTagListFromJson(post: JsonObject): List<String> = post.getString("tags").split(" ")
}

class KonachanPost(id: String, fileUrl: String, rating: String, score: String, tagList: List<String>) : DanbooruPost(
    id,
    fileUrl,
    rating,
    score,
    tagList
) {
    override fun getFullPostUrl() = "https://konachan.com/post/show/$id"
}