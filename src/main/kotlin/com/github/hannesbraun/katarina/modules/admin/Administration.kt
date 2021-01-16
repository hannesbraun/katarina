package com.github.hannesbraun.katarina.modules.admin

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.utilities.*
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.concurrent.TimeUnit

class Administration(private val config: KatarinaConfiguration) : KatarinaModule(), MessageReceivedHandler {
    private val parser = AdministrationParser(config)

    // Slowmode time in seconds
    private val slowmodeTime = 30

    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        val command = parser.parse(event.message.contentRaw) ?: return false
        if (!event.isFromGuild && command.type != AdministrationCommandType.SHUTDOWN) {
            throw KatarinaGuildOnlyException("Administrative commands can only be executed on a server.")
        }

        when (command.type) {
            AdministrationCommandType.CLEAR -> clear(event, command)
            AdministrationCommandType.CREATEDUMMY -> createMessages(event)
            AdministrationCommandType.DISCONNECT -> disconnect(event)
            AdministrationCommandType.MASSMOVE -> massmove(event, command)
            AdministrationCommandType.MUTE -> mute(event, true)
            AdministrationCommandType.SHOWPERMISSIONS -> showPermissions(event, command)
            AdministrationCommandType.SHUTDOWN -> shutdown(event)
            AdministrationCommandType.SLOWMODE -> slowmode(event)
            AdministrationCommandType.UNMUTE -> mute(event, false)
        }
        return true
    }

    private fun clear(event: MessageReceivedEvent, command: AdministrationCommand) {
        if (event.member?.hasPermission(Permission.MESSAGE_MANAGE) != true)
            throw KatarinaUnauthorizedException("Not authorized to execute this command")

        event.message.delete().queue {
            // Execute clearing and limit amount of messages to prevent a stack overflow (because of recursion)
            if (command.intArg > 0)
                clearHelper(event, command.intArg.limit(0, 1000 * 100), true)
        }
    }

    // Recursive function to delete the messages because JDA or Discord limits the amount of messages to delete with a single call to 100
    private fun clearHelper(event: MessageReceivedEvent, leftover: Int, initial: Boolean) {
        if (leftover <= 0) return

        val history = event.channel.history
        val leftoverLimited = leftover.limit(0, 100)
        history.retrievePast(leftoverLimited.limit(2, 100)).submitAfter(if (initial) 0 else 2, TimeUnit.SECONDS)
            .thenAccept {
                if (it.isEmpty()) return@thenAccept
                else if (it.size == 1 || leftoverLimited == 1) it[0].delete().submit()
                else event.textChannel.deleteMessages(it).submit()
            }.thenRun { clearHelper(event, leftover - leftoverLimited, false) }
    }

    private fun disconnect(event: MessageReceivedEvent) {
        if (event.member?.hasPermission(Permission.VOICE_MOVE_OTHERS) != true)
            throw KatarinaUnauthorizedException("Not authorized to execute this command")

        for (member in event.message.mentionedMembers) {
            try {
                event.guild.moveVoiceMember(member, null).queue()
            } catch (e: IllegalStateException) {
                event.channel.sendMessage("Unable to disconnect ${member.effectiveName}")
                    .deleteAfter(30, TimeUnit.SECONDS).queue()
            }
        }
    }

    private fun massmove(event: MessageReceivedEvent, command: AdministrationCommand) {
        if (event.member?.hasPermission(Permission.VOICE_MOVE_OTHERS) != true)
            throw KatarinaUnauthorizedException("Not authorized to execute this command")

        fun findVoiceChannelById(guild: Guild, id: String) = guild.voiceChannels.first { it.id == id }
        val destination = findVoiceChannelById(event.guild, command.strArg2)

        for (member in findVoiceChannelById(event.guild, command.strArg1).members) {
            event.guild.moveVoiceMember(member, destination).queue()
        }

        event.message.delete().queue()
    }

    private fun mute(event: MessageReceivedEvent, mute: Boolean) {
        if (event.member?.hasPermission(Permission.VOICE_MUTE_OTHERS) != true)
            throw KatarinaUnauthorizedException("Not authorized to execute this command")

        for (member in event.message.mentionedMembers) {
            try {
                member.mute(mute).queue()
            } catch (e: IllegalStateException) {
                event.channel.sendMessage("Unable to mute ${member.effectiveName}").queue()
            }
        }
    }

    private fun showPermissions(event: MessageReceivedEvent, command: AdministrationCommand) {
        val channel = event.guild.channels.first { it.id == command.strArg1 }
        val member = event.message.mentionedMembers.first()

        val message = Permission.values()
            .joinToString("\n", "```", "```") { "${it.getName()}: ${member.hasPermission(channel, it)}" }

        event.channel.sendMessage(message).queue()
    }

    private fun slowmode(event: MessageReceivedEvent) {
        if (event.member?.hasPermission(Permission.MANAGE_CHANNEL) != true)
            throw KatarinaUnauthorizedException("Not authorized to execute this command")

        if (event.textChannel.slowmode == 0)
            event.textChannel.manager.setSlowmode(slowmodeTime).queue()
        else
            event.textChannel.manager.setSlowmode(0).queue()
    }

    private fun shutdown(event: MessageReceivedEvent) {
        if (event.author.id != config.ownerId)
            throw KatarinaUnauthorizedException("Not authorized to execute this command")
        else
            event.jda.shutdown()
    }

    // For debugging only
    private fun createMessages(event: MessageReceivedEvent) {
        repeat(100) {
            event.channel.sendMessage("Dummy message").queue()
        }
    }
}