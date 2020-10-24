package com.github.hannesbraun.katarina.modules.music

import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class MusicBot : KatarinaModule(), MessageReceivedHandler {
    private val parser = MusicBotParser()

    override fun canHandleMessageReceived(event: MessageReceivedEvent): Boolean = parser.canHandle(event)

    override fun handleMessageReceived(event: MessageReceivedEvent) {
        val command = parser.getCommand(event)
        when (command.baseCommand) {
            MusicBotBaseCommand.PLAY -> play()
        }
    }

    private fun play() {
    }

}