package com.github.hannesbraun.katarina.modules.danbooru

import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random

class GelbooruClient(scope: CoroutineScope) : DanbooruClient(scope) {
    override val indexUrl = "https://gelbooru.com/index.php"
    override val name = "Gelbooru"

    override fun getIndexParameters(): List<Pair<String, String>> {
        val pid = Random.nextInt(1, 201)
        return listOf(
            Pair("page", "dapi"),
            Pair("s", "post"),
            Pair("q", "index"),
            Pair("json", "1"),
            Pair("limit", postBufferSize.toString()),
            Pair("pid", pid.toString())
        )
    }

    override fun generatePost(
        id: String, fileUrl: String, rating: String, score: String, tagList: List<String>
    ) = GelbooruPost(id, fileUrl, rating, score, tagList)

    override fun getTagListFromJson(post: JsonObject): List<String> = post.getString("tags").split(" ")
}

class GelbooruPost(id: String, fileUrl: String, rating: String, score: String, tagList: List<String>) : DanbooruPost(
    id,
    fileUrl,
    rating,
    score,
    tagList
) {
    override fun getFullPostUrl() = "https://gelbooru.com/index.php?page=post&s=view&id=$id"
}