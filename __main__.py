import sys
import discord
import rlc
import reaction

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
        elif len(message.content) >= 2:
            if message.content[-1] == "?" and message.content[-2] != "!" and not message.content.startswith("!"):
                # Add a random yes/no reaction to the message containing a question mark
                selected_reaction = reaction.get_rand_reaction()
                await message.add_reaction(selected_reaction)


if __name__ == '__main__':
    if len(sys.argv) >= 2:
        client = Katarina()
        client.run(sys.argv[1])
    else:
        print("Please specify a token fot the bot.")
