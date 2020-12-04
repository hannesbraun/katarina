package com.github.hannesbraun.katarina.modules.danbooru

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaParser

class DanbooruParser(config: KatarinaConfiguration) : KatarinaParser(config) {
    fun parse(message: String): DanbooruCommand {
        val arg = splitArgs(message)[0].toLowerCase()
        return DanbooruCommand(DanbooruSite.fromString(arg.removeSuffix("+")), arg[arg.length - 1] == '+')
    }
}

data class DanbooruCommand(val site: DanbooruSite?, val explicitOnly: Boolean)

enum class DanbooruSite(val commandRaw: String) {
    DANBOORU("danbooru"),
    E621("e621"),
    FURRYBOORU("furrybooru"),
    GELBOORU("gelbooru"),
    KONACHAN("konachan"),
    RULE34("rule34"),
    SAFEBOORU("safebooru"),
    YANDERE("yandere");

    companion object {
        private val map = values().associateBy(DanbooruSite::commandRaw)
        fun fromString(commandRaw: String) = map[commandRaw]
    }
}
