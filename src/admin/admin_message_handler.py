import discord

from message_handler import MessageHandler


class AdminMessageHandler(MessageHandler):
    async def _clear(self, full_msg):
        if full_msg.author.permissions_in(full_msg.channel).manage_messages:
            # Get first argument: amount of messages to delete
            arg1 = self._get_argument(full_msg.content, 1, self.cmd_prefix)
            if len(arg1) <= 0:
                await full_msg.delete()
                return
            try:
                arg1_int = int(arg1)
            except ValueError:
                await full_msg.delete()
                return

            messages = await full_msg.channel.history(limit=arg1_int).flatten()
            await full_msg.channel.delete_messages(messages)

    async def _mute(self, full_msg):
        if full_msg.author.guild_permissions.mute_members:
            for user in full_msg.mentions:
                await user.edit(mute=True)
            if len(full_msg.mentions) > 0:
                await full_msg.channel.send("The specified users were muted.")

    async def _slowmode(self, full_msg):
        if full_msg.author.permissions_in(full_msg.channel).manage_channels:
            if full_msg.channel.slowmode_delay == 0:
                await full_msg.channel.edit(slowmode_delay=30.0)
            else:
                await full_msg.channel.edit(slowmode_delay=0.0)

    async def _unmute(self, full_msg):
        if full_msg.author.guild_permissions.mute_members:
            for user in full_msg.mentions:
                await user.edit(mute=False)
            if len(full_msg.mentions) > 0:
                await full_msg.channel.send("The specified users were unmuted.")

    commands = {
        "clear": _clear,
        "mute": _mute,
        "slowmode": _slowmode,
        "unmute": _unmute
    }

    @classmethod
    def can_handle(cls, full_msg, cmd_prefix):
        if cls._get_argument(full_msg, 0, cmd_prefix).lower() in cls.commands:
            return True
        else:
            return False

    async def handle_message(self, full_msg):
        if not isinstance(full_msg.channel, discord.TextChannel):
            # Only for servers
            return

        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix).lower()
        await self.commands[arg0](self, full_msg)
