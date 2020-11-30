package com.github.hannesbraun.katarina.modules.danbooru

import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random

class Rule34Client(private val scope: CoroutineScope) : DanbooruClient(scope) {
    override val name = "Rule 34"
    override val indexUrl = "https://rule34.xxx/index.php"

    override fun getIndexParameters(): List<Pair<String, String>> {
        val pid = Random.nextInt(1, 2001)
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
    ) = Rule34Post(id, fileUrl, rating, score, tagList)

    override fun getTagListFromJson(post: JsonObject): List<String> = post.getString("tags").split(" ")
    override fun getFileUrlFromJson(post: JsonObject) =
        "https://rule34.xxx/images/${post.getString("directory")}/${post.getString("image")}"
}

class Rule34Post(id: String, fileUrl: String, rating: String, score: String, tagList: List<String>) : DanbooruPost(
    id,
    fileUrl,
    rating,
    score,
    tagList
) {
    override fun getFullPostUrl() = "https://rule34.xxx/index.php?page=post&s=view&id=$id"
    override fun isExplicit() = rating.toLowerCase() == "explicit"
}