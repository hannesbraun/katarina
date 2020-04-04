import discord

from message_handler import MessageHandler


class CcsMessageHandler(MessageHandler):
    cooldownList = {}

    @classmethod
    def can_handle(cls, full_msg, db_connection_wrapper, configuration):
        arg0 = cls._get_argument(full_msg.content, 0, configuration.cmd_prefix)

        if full_msg.channel.type != discord.ChannelType.text:
            # Only works inside guild text channels
            return

        if arg0.lower() == "setcc" or arg0.lower() == "rmcc" or arg0.lower() == "ccs-help":
            # Admin commands
            return True

        # Check if command is available
        db_connection_wrapper.lockConnection()
        cursor = db_connection_wrapper.connection.cursor()
        cursor.execute("SELECT COUNT(*) FROM classic_command WHERE command = ? and server_id = ? and active = 1", (arg0,
                       str(full_msg.channel.guild.id)))
        result = cursor.fetchone()[0]
        db_connection_wrapper.unlockConnection()

        if result > 0:
            return True
        else:
            return False

    def _is_allowed(self, full_msg):
        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix)

        # Get restrictions for command
        self.db_connection_wrapper.lockConnection()
        cursor = self.db_connection_wrapper.connection.cursor()
        cursor.execute("SELECT * FROM classic_command_restriction WHERE command = ? and server_id = ?", (arg0,
                       str(full_msg.channel.guild.id)))
        restriction_list = cursor.fetchall()
        self.db_connection_wrapper.unlockConnection()

        # Default: command is allowed
        allowed_user = True
        allowed_channel = True
        user_whitelist_mode = False
        channel_whitelist_mode = False

        for restriction in restriction_list:
            try:
                affected_id = int(restriction["affected_id"])
            except ValueError:
                continue

            if restriction["type"] == "uw":
                # User whitelist
                if not user_whitelist_mode:
                    allowed_user = False
                user_whitelist_mode = True

                if affected_id == full_msg.author.id:
                    allowed_user = True

            elif restriction["type"] == "ub":
                # User blacklist
                if user_whitelist_mode:
                    continue

                if affected_id == full_msg.author.id:
                    allowed_user = False

            elif restriction["type"] == "cw":
                # Channel whitelist
                if not channel_whitelist_mode:
                    allowed_channel = False
                channel_whitelist_mode = True

                if affected_id == full_msg.channel.guild.id:
                    allowed_channel = True

            elif restriction["type"] == "cb":
                # Channel blacklist
                if channel_whitelist_mode:
                    continue

                if affected_id == full_msg.channel.guild.id:
                    allowed_channel = False

        return allowed_channel and allowed_user

    def _get_cc_help_list(self, server_id, server_name):
        # Get command list
        self.db_connection_wrapper.lockConnection()
        cursor = self.db_connection_wrapper.connection.cursor()
        cursor.execute("SELECT * FROM classic_command WHERE server_id = ? and active = 1", (str(server_id),))
        command_list = cursor.fetchall()
        self.db_connection_wrapper.unlockConnection()

        embed_list = []
        embed = discord.Embed(title="Available classic commands for " + server_name,
                              colour=discord.Colour(0x421497))

        for command in command_list:
            # Get description
            description = command["description"]
            if description is None or len(description) <= 0 or len(description) > 1000:
                description =  "*No description*"

            # Add command
            embed.add_field(name=self.cmd_prefix + command["command"], value=description)

            if len(embed) > 6000:
                # Limit reached, start new embed
                embed.remove_field(len(embed.fields) - 1)
                embed_list.append(embed)
                embed = discord.Embed(title="Available classic commands for " + server_name,
                                      colour=discord.Colour(0x421497))
                embed.add_field(name=self.cmd_prefix + command["command"], value=description)

        embed_list.append(embed)
        return embed_list

    async def handle_message(self, full_msg):
        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix)

        if full_msg.channel.type != discord.ChannelType.text:
            # Only works inside guild text channels
            return

        if arg0.lower() == "ccs-help":
            # Send list with available commands for the server
            help_msg_list = self._get_cc_help_list(full_msg.channel.guild.id, full_msg.channel.guild.name)
            for help_msg in help_msg_list:
                await full_msg.author.send(embed=help_msg)

        if not self._is_allowed(full_msg):
            # Not allowed due to user or channel restrictions
            return

        # Get command
        self.db_connection_wrapper.lockConnection()
        cursor = self.db_connection_wrapper.connection.cursor()
        cursor.execute("SELECT * FROM classic_command WHERE command = ? and server_id = ? and active = 1", (arg0,
                       str(full_msg.channel.guild.id)))
        result = cursor.fetchone()
        self.db_connection_wrapper.unlockConnection()

        if result is None:
            # Nothing found (not really necessary in most cases)
            return

        if result["nsfw"] == 1 and not full_msg.channel.is_nsfw():
            await full_msg.channel.send("The requested content is not safe for work. Please consider invoking this " +
                                        "command in an appropriate channel.")
        else:
            await full_msg.channel.send(result["message"])
