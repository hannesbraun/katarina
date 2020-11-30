package com.github.hannesbraun.katarina.modules.danbooru

import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.utilities.KatarinaGuildOnlyException
import com.github.hannesbraun.katarina.utilities.KatarinaNSFWException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Danbooru(private val scope: CoroutineScope) : KatarinaModule(), MessageReceivedHandler {
    private val parser = DanbooruParser()

    private val providers =
        mapOf(
            DanbooruSite.DANBOORU to DanbooruClient(scope),
            DanbooruSite.E621 to E621Client(scope),
            // DanbooruSite.FURRYBOORU to FurryBooruClient(scope),
            DanbooruSite.GELBOORU to GelbooruClient(scope),
            DanbooruSite.KONACHAN to KonachanClient(scope),
            DanbooruSite.RULE34 to Rule34Client(scope),
            DanbooruSite.SAFEBOORU to SafebooruClient(scope),
            DanbooruSite.YANDERE to YandereClient(scope)
        )

    init {
        providers.forEach { it.value.init() }
    }

    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        val command = parser.parse(event.message.contentRaw)
        command.site ?: return false

        checkAutorization(event)
        val client = providers[command.site] ?: return true

        scope.launch {
            val post = client.getPostAsEmbed(command.explicitOnly)
            event.channel.sendMessage(post).queue()
        }
        return true
    }

    private fun checkAutorization(event: MessageReceivedEvent) {
        if (!event.isFromGuild) throw KatarinaGuildOnlyException("This command is only allowed to be invoked on a server since private channels are not marked as NSFW.")
        if (!event.textChannel.isNSFW) throw KatarinaNSFWException("This command is not safe for work. You can only invoke it in a channel marked as NSFW.")
    }
}