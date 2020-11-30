package com.github.hannesbraun.katarina.modules.danbooru

import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random

class YandereClient(scope: CoroutineScope) : DanbooruClient(scope) {
    override val indexUrl = "https://yande.re/post.json"
    override val name = "Yande.re"

    override fun getIndexParameters(): List<Pair<String, String>> {
        val page = Random.nextInt(1, 6250)
        return listOf(Pair("limit", postBufferSize.toString()), Pair("page", page.toString()))
    }

    override fun generatePost(
        id: String, fileUrl: String, rating: String, score: String, tagList: List<String>
    ) = YanderePost(id, fileUrl, rating, score, tagList)

    override fun getTagListFromJson(post: JsonObject): List<String> = post.getString("tags").split(" ")
}

class YanderePost(id: String, fileUrl: String, rating: String, score: String, tagList: List<String>) : DanbooruPost(
    id,
    fileUrl,
    rating,
    score,
    tagList
) {
    override fun getFullPostUrl() = "https://yande.re/post/show/$id"
}
