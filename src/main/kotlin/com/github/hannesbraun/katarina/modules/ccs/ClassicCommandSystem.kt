package com.github.hannesbraun.katarina.modules.ccs

import com.github.hannesbraun.katarina.KatarinaConfiguration
import com.github.hannesbraun.katarina.database.ClassicCommand
import com.github.hannesbraun.katarina.database.ClassicCommandRestriction
import com.github.hannesbraun.katarina.modules.KatarinaModule
import com.github.hannesbraun.katarina.modules.MessageReceivedHandler
import com.github.hannesbraun.katarina.utilities.KatarinaCCSException
import com.github.hannesbraun.katarina.utilities.limitWithDots
import com.github.hannesbraun.katarina.utilities.toBoolean
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.TimeUnit

class ClassicCommandSystem(private val database: Database, private val config: KatarinaConfiguration) :
    KatarinaModule(), MessageReceivedHandler {
    private val parser = ClassicCommandSystemParser(config)

    override fun tryHandleMessageReceived(event: MessageReceivedEvent): Boolean {
        // Just ignoring the event in case of the message being set in a private channel
        // Complaining with a KatarinaException would probably need a lot of error handling and doesn't really help a lot
        if (!event.isFromGuild) return false

        val parsedCommand = parser.parse(event.message.contentRaw)
        if (!parsedCommand.meta) {
            return handleNonMetaCommand(event, parsedCommand)
        } else {
            if (event.guild.owner?.isOwner != true && parsedCommand.name != ClassicMetaCommand.help) throw KatarinaCCSException(
                "Unauthorized. Only the server owner can execute this command."
            )

            when (parsedCommand.name) {
                ClassicMetaCommand.set -> handleSetcc(event, parsedCommand)
                ClassicMetaCommand.remove -> deleteCommand(event, parsedCommand)
                ClassicMetaCommand.data -> sendData(event, parsedCommand)
                ClassicMetaCommand.help -> {
                    sendHelp(event)
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                }
            }

            return true
        }
    }

    private fun handleNonMetaCommand(event: MessageReceivedEvent, parsedCommand: ParsedClassicCommand): Boolean {
        var found = false

        transaction(database) {
            val query = ClassicCommand.select {
                (ClassicCommand.command eq parsedCommand.name) and (ClassicCommand.serverId eq event.guild.id)
            }.limit(1)

            val restrictionQuery = ClassicCommandRestriction.select {
                (ClassicCommandRestriction.command eq parsedCommand.name) and (ClassicCommandRestriction.serverId eq event.guild.id)
            }

            query.forEach { it ->
                var userWhitelisted = false
                var channelWhitelisted = false
                var userBlacklisted = false
                var channelBlacklisted = false
                var userWhitelistActive = false
                var channelWhitelistActive = false

                // Check restrictions
                restrictionQuery.forEach {
                    when (RestrictionType.fromString(it[ClassicCommandRestriction.type])) {
                        RestrictionType.USER_WHITELIST -> {
                            userWhitelistActive = true
                            if (it[ClassicCommandRestriction.affectedId] == event.author.id) {
                                userWhitelisted = true
                            }
                        }
                        RestrictionType.USER_BLACKLIST -> {
                            if (it[ClassicCommandRestriction.affectedId] == event.author.id) {
                                userBlacklisted = true
                            }
                        }
                        RestrictionType.CHANNEL_WHITELIST -> {
                            channelWhitelistActive = true
                            if (it[ClassicCommandRestriction.affectedId] == event.channel.id) {
                                channelWhitelisted = true
                            }
                        }
                        RestrictionType.CHANNEL_BLACKLIST -> {
                            if (it[ClassicCommandRestriction.affectedId] == event.channel.id) {
                                channelBlacklisted = true
                            }
                        }
                    }
                }

                if (!it[ClassicCommand.active].toBoolean()) {
                    // Command inactive
                    found = true
                } else if (!isAllowed(
                        userWhitelisted,
                        userWhitelistActive,
                        userBlacklisted,
                        channelWhitelisted,
                        channelWhitelistActive,
                        channelBlacklisted
                    )
                ) {
                    // Not allowed to execute command
                    found = true
                } else if (it[ClassicCommand.nsfw].toBoolean() && !event.textChannel.isNSFW) {
                    // Unable to send: channel is not marked as NSFW
                    event.channel.sendMessage("I can't show you the content of this command because it is marked as NSFW and this channel is not.")
                        .queue()
                    found = true
                } else {
                    event.channel.sendMessage(it[ClassicCommand.message]).queue()
                }
            }
        }

        return found
    }

    private fun isAllowed(
        userWhitelisted: Boolean,
        userWhitelistActive: Boolean,
        userBlacklisted: Boolean,
        channelWhitelisted: Boolean,
        channelWhitelistActive: Boolean,
        channelBlacklisted: Boolean
    ) =
        (userWhitelisted || (!userWhitelistActive && !userBlacklisted)) && (channelWhitelisted || (!channelWhitelistActive && !channelBlacklisted))

    private fun handleSetcc(event: MessageReceivedEvent, parsedCommand: ParsedClassicCommand) {
        transaction(database) {
            if (parsedCommand.action != SetccAction.RESTRICTION && parsedCommand.action != SetccAction.DELETE_RESTRICTION) {
                val existing = !ClassicCommand.select {
                    (ClassicCommand.command eq parsedCommand.affectedCommand) and (ClassicCommand.serverId eq event.guild.id)
                }.empty()

                if (!existing && parsedCommand.action == SetccAction.MESSAGE) {
                    // Command not existing yet
                    // Create new command entry (because of setcc message command)
                    ClassicCommand.insert {
                        it[command] = parsedCommand.affectedCommand
                        it[serverId] = event.guild.id
                        it[message] = parsedCommand.setccValue
                        it[nsfw] = 0
                        it[active] = 1
                    }
                    event.channel.sendMessage("Command `${config.prefix}${parsedCommand.affectedCommand}` created with value:\n${parsedCommand.setccValue}")
                        .queue()
                } else {
                    // Update value for command
                    ClassicCommand.update({
                        (ClassicCommand.command eq parsedCommand.affectedCommand) and (ClassicCommand.serverId eq event.guild.id)
                    }) {
                        when (parsedCommand.action) {
                            SetccAction.MESSAGE -> it[message] = parsedCommand.setccValue
                            SetccAction.DESCRIPTION -> it[description] = parsedCommand.setccValue
                            SetccAction.ACTIVE -> it[active] = parsedCommand.setccValue.toInt()
                            SetccAction.NSFW -> it[nsfw] = parsedCommand.setccValue.toInt()
                            SetccAction.RESTRICTION -> Unit /* Managing restrictions is handled in the next major if blocks in this function */
                            SetccAction.DELETE_RESTRICTION -> Unit
                        }
                    }
                    event.channel.sendMessage("Command `${config.prefix}${parsedCommand.affectedCommand}` (action: ${parsedCommand.action?.action ?: "null"}) updated with value:\n${parsedCommand.setccValue}")
                        .queue()
                }
            } else if (parsedCommand.action == SetccAction.DELETE_RESTRICTION && parsedCommand.restrictionType != null) {
                // Delete restriction
                ClassicCommandRestriction.deleteWhere {
                    (ClassicCommandRestriction.command eq parsedCommand.affectedCommand) and (
                            ClassicCommandRestriction.serverId eq event.guild.id) and (
                            ClassicCommandRestriction.type eq parsedCommand.restrictionType.restriction) and (
                            ClassicCommandRestriction.affectedId eq parsedCommand.affectedId)
                }
                event.channel.sendMessage("Restriction of type ${parsedCommand.restrictionType.restriction} with id ${parsedCommand.affectedId} for command `${config.prefix}${parsedCommand.affectedCommand}` was deleted.")
                    .queue()
            } else if (parsedCommand.action == SetccAction.RESTRICTION && parsedCommand.restrictionType != null) {
                // Create restriction if not existing yet
                // TODO("Does insertIgnore as insert or if existing do nothing?")
                ClassicCommandRestriction.insertIgnore {
                    it[command] = parsedCommand.affectedCommand
                    it[serverId] = event.guild.id
                    it[type] = parsedCommand.restrictionType.restriction
                    it[affectedId] = parsedCommand.affectedId
                }
                event.channel.sendMessage("Restriction of type ${parsedCommand.restrictionType.restriction} with id ${parsedCommand.affectedId} for command `${config.prefix}${parsedCommand.affectedCommand}` was added.")
                    .queue()
            } else {
                // Making Kotlin happy with this empty else branch?
            }
        }
    }

    private fun deleteCommand(event: MessageReceivedEvent, parsedCommand: ParsedClassicCommand) {
        transaction(database) {
            ClassicCommand.deleteWhere {
                (ClassicCommand.command eq parsedCommand.affectedCommand) and (ClassicCommand.serverId eq event.guild.id)
            }
            ClassicCommandRestriction.deleteWhere {
                (ClassicCommandRestriction.command eq parsedCommand.affectedCommand) and (ClassicCommandRestriction.serverId eq event.guild.id)
            }
        }
        event.channel.sendMessage("Command `${config.prefix}${parsedCommand.affectedCommand}` was deleted.").queue()
    }

    private fun sendData(event: MessageReceivedEvent, parsedCommand: ParsedClassicCommand) {
        var description: String? = null
        var active: Boolean = true
        var nsfw: Boolean = false
        var found = false
        val userWhitelist = mutableListOf<String>()
        val userBlacklist = mutableListOf<String>()
        val channelWhitelist = mutableListOf<String>()
        val channelBlacklist = mutableListOf<String>()

        transaction(database) {
            ClassicCommand.select {
                (ClassicCommand.command eq parsedCommand.affectedCommand) and (ClassicCommand.serverId eq event.guild.id)
            }.limit(1).forEach {
                found = true
                description = it[ClassicCommand.description]
                active = it[ClassicCommand.active].toInt().toBoolean()
                nsfw = it[ClassicCommand.nsfw].toInt().toBoolean()
            }

            ClassicCommandRestriction.select {
                (ClassicCommandRestriction.command eq parsedCommand.affectedCommand) and (ClassicCommandRestriction.serverId eq event.guild.id)
            }.forEach {
                when (RestrictionType.fromString(it[ClassicCommandRestriction.type])) {
                    RestrictionType.USER_WHITELIST -> userWhitelist.add(it[ClassicCommandRestriction.affectedId])
                    RestrictionType.USER_BLACKLIST -> userBlacklist.add(it[ClassicCommandRestriction.affectedId])
                    RestrictionType.CHANNEL_WHITELIST -> channelWhitelist.add(it[ClassicCommandRestriction.affectedId])
                    RestrictionType.CHANNEL_BLACKLIST -> channelBlacklist.add(it[ClassicCommandRestriction.affectedId])
                }
            }
        }

        if (!found) return

        if (description == null) {
            description = "**No description**"
        }

        val embed = EmbedBuilder()
            .setTitle("${config.prefix}${parsedCommand.affectedCommand}")
            .setColor(0x421497)
            .setDescription(description)
            .addField("active", active.toString(), true)
            .addField("nsfw", nsfw.toString(), true)
            .addField(
                "User whitelist",
                if (userWhitelist.isNotEmpty()) userWhitelist.joinToString("`, `", "`", "´") else "*empty*",
                true
            )
            .addField(
                "User blacklist",
                if (userBlacklist.isNotEmpty()) userBlacklist.joinToString("`, `", "`", "´") else "*empty*",
                true
            )
            .addField(
                "Channel whitelist",
                if (channelWhitelist.isNotEmpty()) channelWhitelist.joinToString("`, `", "`", "´") else "*empty*",
                true
            )
            .addField(
                "Channel blacklist",
                if (channelBlacklist.isNotEmpty()) channelBlacklist.joinToString("`, `", "`", "´") else "*empty*",
                true
            )
            .build()
        event.channel.sendMessage(embed).queue()
    }

    private fun sendHelp(event: MessageReceivedEvent) {
        val commands = mutableListOf<String>()

        transaction(database) {
            ClassicCommand.select { ClassicCommand.serverId eq event.guild.id }.forEach {
                if (it[ClassicCommand.description] != null) {
                    commands.add(
                        "**${config.prefix}${it[ClassicCommand.command].limitWithDots(1000)}**: *${
                            it[ClassicCommand.description]?.limitWithDots(140)
                        }*"
                    )
                } else {
                    commands.add("**${config.prefix}${it[ClassicCommand.command]}**")
                }
            }
        }

        val baseMessage = "**Available commands for your server:**"
        var message = baseMessage
        val discordMessages = mutableListOf<String>()
        for (command in commands) {
            val newMessage = message + "\n" + command
            message = if (newMessage.length > 2000) {
                discordMessages.add(message)
                baseMessage + "\n" + command
            } else {
                newMessage
            }
        }
        discordMessages.add(message)
        for (discordMessage in discordMessages)
            event.author.openPrivateChannel().flatMap { it.sendMessage(discordMessage) }.queue()
    }
}