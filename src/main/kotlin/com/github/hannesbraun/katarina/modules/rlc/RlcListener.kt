package com.github.hannesbraun.katarina.modules.rlc

import com.github.hannesbraun.katarina.modules.KatarinaListener
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class RlcListener : KatarinaListener() {
    val champions = Champions()

    override fun onMessageReceived(event: MessageReceivedEvent) {
        event.channel.sendMessage(champions.getRandomChampion())
    }
}
