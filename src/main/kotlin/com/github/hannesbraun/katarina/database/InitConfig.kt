package com.github.hannesbraun.katarina.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun initConfiguration(database: Database) {
    transaction(database) {
        insertDefaultConfigurationValue(ConfigurationConstants.keyPrefix, ConfigurationConstants.defaultPrefix)
        insertDefaultConfigurationValue(ConfigurationConstants.keyBotName, ConfigurationConstants.defaultBotName)
        insertDefaultConfigurationValue(ConfigurationConstants.keyOwnerId, ConfigurationConstants.defaultOwnerId)

        // Not completely correct, but since there's only database version one for now, this should work
        // With more versions, the version should not automatically be inserted (only if the whole database was just created from scratch)
        insertDefaultConfigurationValue(ConfigurationConstants.keyDbVersion, ConfigurationConstants.defaultDbVersion)
    }
}

private fun insertDefaultConfigurationValue(key: String, default: String) {
    val value = Configuration.select { Configuration.key eq key }.toList()
    if (value.isEmpty()) {
        Configuration.insert {
            it[Configuration.key] = key
            it[Configuration.value] = default
        }
    }
}
