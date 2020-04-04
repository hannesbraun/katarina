from message_handler import MessageHandler


class MiscMessageHandler(MessageHandler):

    commands = [
        "katarina-source"
    ]

    @classmethod
    def can_handle(cls, raw_msg, cmd_prefix):
        msg_arg0 = cls._get_argument(raw_msg, 0, cmd_prefix).lower()

        for cmd in cls.commands:
            if msg_arg0 == cmd:
                return True

        # Not found in supported commands
        return False

    async def handle_message(self, full_msg):
        command = self._get_argument(full_msg.content, 0, self.cmd_prefix).lower()

        if command == "katarina-source":
            await full_msg.channel.send("Have a look at the code I'm made of: https://github.com/hannesbraun/katarina")
