from message_handler import MessageHandler


class CcsMessageHandler(MessageHandler):
    cooldownList = {}

    @classmethod
    def can_handle(cls, full_msg, db_connection_wrapper, configuration):
        arg0 = cls._get_argument(full_msg.content, 0, configuration.cmd_prefix)

        if arg0.lower() == "setcc" or "rmcc":
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

    async def handle_message(self, full_msg):
        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix)

        if not self._is_allowed(full_msg):
            # Not allowed due to user or channel restrictions
            return

        # Get commands
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
