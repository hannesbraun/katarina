package com.github.hannesbraun.katarina.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object ClassicCommand: Table("classic_command") {
    val command: Column<String> = text("command")
    val serverId: Column<String> = text("server_id")
    val message: Column<String> = text("message")
    val active: Column<Int> = integer("active").default(1)
    val description: Column<String?> = text("description").nullable()
    val nsfw: Column<Int> = integer("nsfw").default(0)
    override val primaryKey = PrimaryKey(command, serverId)
}

object ClassicCommandRestriction: Table("classic_command_restriction") {
    val command: Column<String> = text("command")
    val serverId: Column<String> = text("server_id")
    val type: Column<String> = text("type")
    val affectedId: Column<String> = text("affected_id")
    override val primaryKey = PrimaryKey(command, serverId, type, affectedId)
}

object Configuration: Table("configuration") {
    val key: Column<String> = text("key")
    val value: Column<String> = text("value")
    override val primaryKey = PrimaryKey(key)
}

object Gif: Table("gif") {
    val command: Column<String> = text("command")
    val url: Column<String> = text("url")
    val active: Column<Int> = integer("active")
    val nsfw: Column<Int> = integer("nsfw").default(0)
    override val primaryKey = PrimaryKey(command, url)
}

object Joke: Table("joke") {
    val heading: Column<String?> = text("heading").nullable()
    val text: Column<String> = text("text")
    val active: Column<Int> = integer("active").default(1)
    override val primaryKey = PrimaryKey(text)
}
