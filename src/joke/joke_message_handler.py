import random

from message_handler import MessageHandler


class JokeMessageHandler(MessageHandler):
    name = "joke"

    @classmethod
    def can_handle(cls, full_msg, cmd_prefix):
        if cls._get_argument(full_msg, 0, cmd_prefix).lower() == cls.name:
            return True
        else:
            return False

    async def handle_message(self, full_msg):
        # Get all jokes
        async with self.db_connection_wrapper.lock:
            cursor = self.db_connection_wrapper.connection.cursor()
            cursor.execute("SELECT * FROM joke WHERE active = 1")
            result = cursor.fetchall()

        if len(result) > 0:
            # Select a random joke
            joke = random.choice(result)

            # Format heading
            heading = joke["heading"]
            if heading is None or len(heading) <= 0:
                heading = ""
            else:
                heading = "**" + heading + "**\n\n"

            await full_msg.channel.send(heading + joke["text"])
        else:
            # Be sad :(
            await full_msg.channel.send("I don't know a joke... \U0001f622", delete_after=28.0)
            await full_msg.delete(delay=32.0)
