import sys
import discord
import rlc


class Katarina(discord.Client):
    async def on_ready(self):
        print("Katarina is alive. Press ctrl-C to exit.")

    async def on_message(self, message):
        if message.author == client.user:
            # Ignoring own messages
            return

        if message.content.lower().startswith("!rlc"):
            # Get random League of Legends champion
            await message.channel.send(rlc.get_random_champion())
        elif message.content.lower().startswith("!katarina-source"):
            await message.channel.send("Have a look at the code I'm made of: https://github.com/hannesbraun/katarina")


if __name__ == '__main__':
    if len(sys.argv) >= 2:
        client = Katarina()
        client.run(sys.argv[1])
    else:
        print("Please specify a token fot the bot.")
