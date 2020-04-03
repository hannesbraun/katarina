from message_handler import MessageHandler


class MiscMessageHandler(MessageHandler):

    commands = [
        "katarina-source"
    ]

    def __init__(self, configuration):
        self.cmd_prefix = configuration.cmd_prefix

    @classmethod
    def can_handle(cls, msg):
        for cmd in cls.commands:
            if msg.lower() == cmd or msg.lower().startswith(cmd + " "):
                return True

        # Not found in supported commands
        return False

    async def handle_message(self, full_msg):
        raw_msg = full_msg.content[len(self.cmd_prefix):]

        if (raw_msg.lower() == "katarina-source") | raw_msg.lower().startswith("katarina-source "):
            await full_msg.channel.send("Have a look at the code I'm made of: https://github.com/hannesbraun/katarina")
