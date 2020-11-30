package com.github.hannesbraun.katarina.modules.danbooru

import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random

class SafebooruClient(private val scope: CoroutineScope) : DanbooruClient(scope) {
    override val indexUrl = "https://safebooru.org/index.php"
    override val name = "Safebooru"

    override fun getIndexParameters(): List<Pair<String, String>> {
        val pid = Random.nextInt(1, 31000)
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
    ) = SafebooruPost(id, fileUrl, rating, score, tagList)

    override fun getTagListFromJson(post: JsonObject): List<String> = post.getString("tags").split(" ")
    override fun getFileUrlFromJson(post: JsonObject) =
        "https://safebooru.org/images/${post.getString("directory")}/${post.getString("image")}"
}

class SafebooruPost(id: String, fileUrl: String, rating: String, score: String, tagList: List<String>) : DanbooruPost(
    id,
    fileUrl,
    rating,
    score,
    tagList
) {
    override fun getFullPostUrl() = "https://safebooru.org/index.php?page=post&s=view&id=$id"
    override fun isExplicit() = rating.toLowerCase() == "explicit"
}