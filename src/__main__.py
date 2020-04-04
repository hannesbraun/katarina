import sys
import discord

from db_connection import DatabaseConnectionWrapper
from configuration import Configuration
from stage_one_parser import StageOneParser


class Katarina(discord.Client):
    async def on_ready(self):
        print("Katarina is alive...")

    async def on_message(self, message):
        if message.author == self.user:
            # Ignoring own messages
            return

        global db_connection_wrapper
        global configuration

        if message.content == configuration.cmd_prefix + "shutdown" and message.author.id == configuration.owner_id and message.channel.type == discord.ChannelType.private:
            # Initiate shutdown
            await self.close()
            return

        parser = StageOneParser(configuration, db_connection_wrapper)
        msg_handler = parser.get_message_handler(message)

        if msg_handler is not None:
            await msg_handler.handle_message(message)


if __name__ == '__main__':
    if len(sys.argv) >= 2:
        client = Katarina()

        # Connect to database
        db_connection_wrapper = DatabaseConnectionWrapper("katarina.db")

        # Get configuration
        configuration = Configuration(db_connection_wrapper)

        # Run
        client.run(sys.argv[1])

        db_connection_wrapper.shutdown()
    else:
        print("Please specify a token fot the bot.")
