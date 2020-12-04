package com.github.hannesbraun.katarina.modules.danbooru

import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random

class FurryBooruClient(scope: CoroutineScope) : DanbooruClient(scope) {
    override val name = "FurryBooru"
    override val indexUrl = "https://furry.booru.org/index.php"

    override fun getIndexParameters(): List<Pair<String, String>> {
        val pid = Random.nextInt(1, 22600)
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
    ) = FurryBooruPost(id, fileUrl, rating, score, tagList)

    override fun getTagListFromJson(post: JsonObject): List<String> = post.getString("tags").split(" ")
    override fun getFileUrlFromJson(post: JsonObject) =
        "https://furry.booru.org/images/${post.getString("directory")}/${post.getString("image")}"
}

class FurryBooruPost(id: String, fileUrl: String, rating: String, score: String, tagList: List<String>) : DanbooruPost(
    id,
    fileUrl,
    rating,
    score,
    tagList
) {
    override fun getFullPostUrl() = "https://furry.booru.org/index.php?page=post&s=view&id=$id"
    override fun isExplicit() = rating.toLowerCase() == "explicit"
}