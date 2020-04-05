import discord

from message_handler import MessageHandler


class CcsMessageHandler(MessageHandler):
    set_name = "setcc"
    rm_name = "rmcc"
    data_name = "ccdata"
    help_name = "ccs-help"

    @classmethod
    async def can_handle(cls, full_msg, db_connection_wrapper, configuration):
        arg0 = cls._get_argument(full_msg.content, 0, configuration.cmd_prefix)

        if full_msg.channel.type != discord.ChannelType.text:
            # Only works inside guild text channels
            return False

        if arg0.lower() == cls.set_name or arg0.lower() == cls.rm_name or arg0.lower() == cls.data_name or arg0.lower() == cls.help_name:
            # Admin commands
            return True

        # Check if command is available
        async with db_connection_wrapper.lock:
            cursor = db_connection_wrapper.connection.cursor()
            cursor.execute("SELECT COUNT(*) FROM classic_command WHERE command = ? and server_id = ? and active = 1",
                           (arg0, str(full_msg.channel.guild.id)))
            result = cursor.fetchone()[0]

        if result > 0:
            return True
        else:
            return False

    async def _is_allowed(self, full_msg):
        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix)

        # Get restrictions for command
        async with self.db_connection_wrapper.lock:
            cursor = self.db_connection_wrapper.connection.cursor()
            cursor.execute("SELECT * FROM classic_command_restriction WHERE command = ? and server_id = ?",
                           (arg0, str(full_msg.channel.guild.id)))
            restriction_list = cursor.fetchall()

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

                if affected_id == full_msg.channel.id:
                    allowed_channel = True

            elif restriction["type"] == "cb":
                # Channel blacklist
                if channel_whitelist_mode:
                    continue

                if affected_id == full_msg.channel.id:
                    allowed_channel = False

        return allowed_channel and allowed_user

    async def _get_cc_help_list(self, server_id, server_name):
        # Get command list
        async with self.db_connection_wrapper.lock:
            cursor = self.db_connection_wrapper.connection.cursor()
            cursor.execute("SELECT * FROM classic_command WHERE server_id = ? and active = 1", (str(server_id),))
            command_list = cursor.fetchall()

        embed_list = []
        embed = discord.Embed(title="Available classic commands for " + server_name,
                              colour=discord.Colour(0x421497))

        for command in command_list:
            # Get description
            description = command["description"]
            if description is None or len(description) <= 0 or len(description) > 1000:
                description =  "*No description*"

            # Add command
            embed.add_field(name=self.cmd_prefix + command["command"], value=description, inline=False)

            if len(embed) > 6000:
                # Limit reached, start new embed
                embed.remove_field(len(embed.fields) - 1)
                embed_list.append(embed)
                embed = discord.Embed(title="Available classic commands for " + server_name,
                                      colour=discord.Colour(0x421497))
                embed.add_field(name=self.cmd_prefix + command["command"], value=description, inline=False)

        embed_list.append(embed)
        return embed_list

    async def _setcc(self, full_msg):
        # Set command

        arg1 = self._get_argument(full_msg.content, 1, self.cmd_prefix)
        arg2 = self._get_argument(full_msg.content, 2, self.cmd_prefix)

        tmp_msg = full_msg.content[len(self.cmd_prefix):]
        arg_list = tmp_msg.split()
        if len(arg_list) > 3:
            arg3 = " ".join(arg_list[3:])
        else:
            arg3 = ""

        if len(arg1) <= 0 or len(arg2) <= 0 or len(arg3) <= 0:
            # Not enough arguments
            await full_msg.channel.send("```" + self.set_name + ": not enough arguments```", delete_after=11.0)
            return

        if full_msg.author.id != full_msg.channel.guild.owner.id:
            # Not authorized
            await full_msg.channel.send("```" + self.set_name + ": not authorized```", delete_after=11.0)
            return

        result_message = ""
        del_result_message = False

        async with self.db_connection_wrapper.lock:
            cursor = self.db_connection_wrapper.connection.cursor()

            if arg2.lower() == "m":
                # Set message

                # Is command existing?
                cursor.execute("SELECT COUNT(*) FROM classic_command WHERE command = ? and server_id = ?",
                               (arg1, str(full_msg.channel.guild.id)))
                existing = cursor.fetchone()[0]

                # Update or insert data
                if existing >= 1:
                    cursor.execute("UPDATE classic_command SET message = ? WHERE command = ? and server_id = ?",
                                   (arg3, arg1, str(full_msg.channel.guild.id)))
                else:
                    cursor.execute("INSERT INTO classic_command (command, server_id, message) VALUES(?, ?, ?)",
                                   (arg1, str(full_msg.channel.guild.id), arg3))

                if cursor.rowcount > 0:
                    result_message = "```Message for command " + self.cmd_prefix + arg1 + " has been set to "\
                                     + arg3 + ".```"
                else:
                    result_message =  "```" + self.set_name + ": an error ocurred while setting the message for the command```"

            elif arg2.lower() == "a" or arg2.lower() == "nsfw":
                # Set active or nsfw flag for message (pretty similar)
                int_error = False

                try:
                    arg3_int = int(arg3)
                    if arg3_int != 0 and arg3_int != 1:
                        arg3_int = 1
                except ValueError:
                    result_message = "```" + self.set_name + ": invalid argument value```"
                    int_error = True
                    del_result_message = True

                if not int_error:
                    if arg2.lower() == "a":
                        # Active
                        cursor.execute("UPDATE classic_command SET active = ? WHERE command = ? and server_id = ?",
                                       (arg3_int, arg1, str(full_msg.channel.guild.id)))
                    else:
                        # NSFW
                        cursor.execute("UPDATE classic_command SET nsfw = ? WHERE command = ? and server_id = ?",
                                        (arg3_int, arg1, str(full_msg.channel.guild.id)))

                    if cursor.rowcount <= 0:
                        result_message = "No message has been updated."
                    else:
                        if arg2.lower() == "a":
                            result_message = self.cmd_prefix + arg1 + " is now " + ("inactive", "active")[arg3_int]\
                                             + "."
                        else:
                            result_message = self.cmd_prefix + arg1 + " is now" + (" ", " not ")[arg3_int]\
                                             + "safe for work."

            elif arg2.lower() == "d":
                # Update description
                if len(arg3) > 1000:
                    # Limit description length
                    arg3 = arg3[:1000]
                cursor.execute("UPDATE classic_command SET description = ? WHERE command = ? and server_id = ?",
                                   (arg3, arg1, str(full_msg.channel.guild.id)))
                if cursor.rowcount <= 0:
                    result_message = "No message has been updated."
                else:
                    result_message = "Description for " + self.cmd_prefix + arg1 + " has been set."
            elif arg2.lower() == "rmr":
                # Remove restriction
                arg3 = self._get_argument(full_msg.content, 3, self.cmd_prefix)
                arg4 = self._get_argument(full_msg.content, 4, self.cmd_prefix)

                cursor.execute("DELETE FROM classic_command_restriction WHERE command = ? and server_id = ?"
                               + "and type = ? and affected_id = ?", (arg1, str(full_msg.channel.guild.id), arg3, arg4))
                if cursor.rowcount <= 0:
                    result_message = "No restriction has been removed."
                else:
                    result_message = "Successfully deleted " + str(cursor.rowcount) + " restriction(s)."
            elif arg2.lower() == "r":
                # Update restriction
                arg3 = self._get_argument(full_msg.content, 3, self.cmd_prefix).lower()
                arg4 = self._get_argument(full_msg.content, 4, self.cmd_prefix)

                # Only allow valid restriction types: avoids trash in database
                if arg3 != "uw" and arg3 != "ub" and arg3 != "cw" and arg3 != "cb":
                    result_message = "```" + self.set_name + ": invalid restriction type \"" + arg3 + "\"```"
                    type_valid = False
                    del_result_message = True
                else:
                    type_valid = True

                if type_valid:
                    # Is restriction existing?
                    cursor.execute("SELECT COUNT(*) FROM classic_command_restriction WHERE command = ? and server_id ="
                                   + " ? and type = ? and affected_id = ?", (arg1, str(full_msg.channel.guild.id), arg3,
                                                                             arg4))
                    existing = cursor.fetchone()[0]

                    # Update or insert data
                    if existing < 1:
                        cursor.execute("INSERT INTO classic_command_restriction (command, server_id, type, affected_id)"
                                       + " VALUES(?, ?, ?, ?)", (arg1, str(full_msg.channel.guild.id), arg3, arg4))
                        if cursor.rowcount > 0:
                            result_message = "```Restriction for command " + self.cmd_prefix + arg1 + " has been set.```"
                        else:
                            result_message = "```" + self.set_name + ": an error ocurred while setting the restriction for the command```"
                    else:
                        result_message = "```Restriction for command " + self.cmd_prefix + arg1 + " has already been set.```"

            self.db_connection_wrapper.connection.commit()

        if del_result_message:
            await full_msg.channel.send(result_message, delete_after=11.0)
        else:
            await full_msg.channel.send(result_message)


    async def _ccdata(self, full_msg):
        arg1 = self._get_argument(full_msg.content, 1, self.cmd_prefix)

        if len(arg1) <= 0:
            # Not enough arguments
            await full_msg.channel.send("```" + self.data_name + ": not enough arguments```", delete_after=11.0)
            return

        if full_msg.author.id != full_msg.channel.guild.owner.id:
            # Not authorized
            await full_msg.channel.send("```" + self.data_name + ": not authorized```", delete_after=11.0)
            return

        async with self.db_connection_wrapper.lock:
            cursor = self.db_connection_wrapper.connection.cursor()

            # Get basic data
            cursor.execute("SELECT * FROM classic_command WHERE command = ? and server_id = ?",
                           (arg1, str(full_msg.channel.guild.id)))
            command = cursor.fetchone()

            # Get restrictions
            cursor.execute("SELECT * FROM classic_command_restriction WHERE command = ? and server_id = ?"
                           + "and type = 'uw'", (arg1, str(full_msg.channel.guild.id)))
            uw_restrictions = cursor.fetchall()
            cursor.execute("SELECT * FROM classic_command_restriction WHERE command = ? and server_id = ?"
                           + "and type = 'ub'", (arg1, str(full_msg.channel.guild.id)))
            ub_restrictions = cursor.fetchall()
            cursor.execute("SELECT * FROM classic_command_restriction WHERE command = ? and server_id = ?"
                           + "and type = 'cw'", (arg1, str(full_msg.channel.guild.id)))
            cw_restrictions = cursor.fetchall()
            cursor.execute("SELECT * FROM classic_command_restriction WHERE command = ? and server_id = ?"
                           + "and type = 'cb'", (arg1, str(full_msg.channel.guild.id)))
            cb_restrictions = cursor.fetchall()

        description = command["description"]
        if description is None or len(description) <= 0 or len(description) > 1000:
            description = "*No description*"

        embed = discord.Embed(title=self.cmd_prefix + arg1, colour=discord.Colour(0x421497), description=description)
        embed.add_field(name="active", value=("`false`", "`true`")[command["active"]])
        embed.add_field(name="nsfw", value=("`false`", "`true`")[command["nsfw"]])

        # Restrictions
        id_list = []
        for restriction in uw_restrictions:
            id_list.append(restriction["affected_id"])
        embed.add_field(name="User whitelist", value="`" + "`, `".join(id_list) + "`")
        id_list = []
        for restriction in ub_restrictions:
            id_list.append(restriction["affected_id"])
        embed.add_field(name="User blacklist", value="`" + "`, `".join(id_list) + "`")
        id_list = []
        for restriction in cw_restrictions:
            id_list.append(restriction["affected_id"])
        embed.add_field(name="Channel whitelist", value="`" + "`, ".join(id_list) + "`")
        id_list = []
        for restriction in cb_restrictions:
            id_list.append(restriction["affected_id"])
        embed.add_field(name="Channel blacklist", value="`" + "`, `".join(id_list) + "`")

        await full_msg.channel.send(embed=embed)

    async def handle_message(self, full_msg):
        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix)

        if full_msg.channel.type != discord.ChannelType.text:
            # Only works inside guild text channels
            return

        if arg0.lower() == self.help_name:
            # Send list with available commands for the server
            help_msg_list = await self._get_cc_help_list(full_msg.channel.guild.id, full_msg.channel.guild.name)
            for help_msg in help_msg_list:
                await full_msg.author.send(embed=help_msg)

        elif arg0.lower() == self.rm_name:
            # Delete command
            arg1 = self._get_argument(full_msg.content, 1, self.cmd_prefix)
            if len(arg1) <= 0:
                # Not enough arguments
                await full_msg.channel.send("```" + self.rm_name + ": not enough arguments```", delete_after=11.0)
                return

            if full_msg.author.id != full_msg.channel.guild.owner.id:
                # Not authorized
                await full_msg.channel.send("```" + self.rm_name + ": not authorized```", delete_after=11.0)
                return

            async with self.db_connection_wrapper.lock:
                cursor = self.db_connection_wrapper.connection.cursor()
                cursor.execute("DELETE FROM classic_command WHERE command = ? and server_id = ?",
                               (arg1, str(full_msg.channel.guild.id)))
                deleted_entries = cursor.rowcount
                cursor.execute("DELETE FROM classic_command_restriction WHERE command = ? and server_id = ?",
                               (arg1, str(full_msg.channel.guild.id)))
                deleted_restrictions = cursor.rowcount
                self.db_connection_wrapper.connection.commit()

            await full_msg.channel.send("I deleted " + str(deleted_entries) + " command(s) and " + str(deleted_restrictions)
                                        + " restriction(s).")

        elif arg0.lower() == self.set_name:
            await self._setcc(full_msg)

        elif arg0.lower() == self.data_name:
            await self._ccdata(full_msg)

        else:
            if not (await self._is_allowed(full_msg)):
                # Not allowed due to user or channel restrictions
                return

            # Get command
            async with self.db_connection_wrapper.lock:
                cursor = self.db_connection_wrapper.connection.cursor()
                cursor.execute("SELECT * FROM classic_command WHERE command = ? and server_id = ? and active = 1",
                               (arg0, str(full_msg.channel.guild.id)))
                result = cursor.fetchone()

            if result is None:
                # Nothing found (not really necessary in most cases)
                return

            if result["nsfw"] == 1 and not full_msg.channel.is_nsfw():
                await full_msg.channel.send("The requested content is not safe for work. Please consider invoking this " +
                                            "command in an appropriate channel.")
            else:
                await full_msg.channel.send(result["message"])
