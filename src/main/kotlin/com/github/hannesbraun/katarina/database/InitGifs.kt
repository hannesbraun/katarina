package com.github.hannesbraun.katarina.database

import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.lang.ClassCastException
import java.lang.IllegalStateException

fun insertDefaultGifs(database: Database) {
    val gifTypes = com.github.hannesbraun.katarina.modules.gif.Gif.values()

    transaction(database) {
        for (gif in gifTypes) {
            LoggerFactory.getLogger("GifInit").info("Inserting ${gif.asString} GIFs")
            val urls = readGifUrls(gif.asString)

            for (url in urls) {
                Gif.insertIgnore {
                    it[command] = gif.asString
                    it[Gif.url] = url
                    it[active] = 1
                    it[nsfw] = 0
                }
            }
        }
    }
}

private fun readGifUrls(type: String): List<String> {
    val jsonString = object {}.javaClass.getResource("/gifs/${type}.json").readText()

    return try {
        JsonParser.parseString(jsonString).asJsonArray.toList().map { it.asString }
    } catch (e: JsonParseException) {
        emptyList()
    } catch (e: JsonSyntaxException) {
        emptyList()
    } catch (e: IllegalStateException) {
        emptyList()
    } catch (e: ClassCastException) {
        emptyList()
    }
}
