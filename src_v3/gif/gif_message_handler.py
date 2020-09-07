import random

import discord

from message_handler import MessageHandler


class GifMessageHandler(MessageHandler):
    def __init__(self, global_config, db_connection_wrapper):
        super().__init__(global_config, db_connection_wrapper)
        self.bot_name = global_config.bot_name

    _active_commands = {
        "bite": "bites",
        "cake": "feeds",
        "cuddle": "cuddles",
        "glare": "glares at",
        "highfive": "high-fives",
        "hug": "hugs",
        "kiss": "kisses",
        "lick": "licks",
        "pat": "pats",
        "poke": "pokes",
        "punch": "punches",
        "slap": "slaps"
    }
    _passive_commands = [
        "confused",
        "cry",
        "lewd",
        "pout",
        "smug",
        "stare"
    ]

    @classmethod
    def can_handle(cls, full_msg, cmd_prefix):
        arg0 = cls._get_argument(full_msg, 0, cmd_prefix).lower()
        if arg0 in cls._active_commands or arg0 in cls._passive_commands:
            return True
        else:
            return False

    async def handle_message(self, full_msg):
        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix).lower()

        # Get appropriate gifs
        async with self.db_connection_wrapper.lock:
            cursor = self.db_connection_wrapper.connection.cursor()
            if not isinstance(full_msg.channel, discord.TextChannel):
                # Nsfw gifs only work on servers (because DM channels can't be marked as nsfw)
                nsfw = False
            elif not full_msg.channel.is_nsfw():
                # Channel is not marked as nsfw
                nsfw = False
            else:
                nsfw = True

            if not nsfw:
                # Only safe for work gifs
                cursor.execute("SELECT url FROM gif WHERE command = ? and active = 1 and nsfw = 0", (arg0, ))
            else:
                cursor.execute("SELECT url FROM gif WHERE command = ? and active = 1", (arg0, ))
            url_list = cursor.fetchall()

        if len(url_list) > 0:
            # Select a random gif
            gif_url = random.choice(url_list)[0]

            if arg0 in self._active_commands:
                # Get author name
                author_str = full_msg.author.display_name

                if len(full_msg.mentions) <= 0 or not isinstance(full_msg.channel, discord.TextChannel):
                    title = self.bot_name + " " + self._active_commands[arg0] + " " + author_str
                else:
                    # One or more users affected
                    affected_user_list = []
                    for member in full_msg.mentions:
                        affected_user_list.append(member.display_name)
                    if len(affected_user_list) > 1:
                        title = author_str + " " + self._active_commands[arg0] + " " + ", ".join(affected_user_list[:-1])\
                                + " and " + affected_user_list[-1]
                    else:
                        title = author_str + " " + self._active_commands[arg0] + " " + affected_user_list[0]

                funny_embed = discord.Embed(title=title, colour=discord.Colour(0x4a6cac))
            else:
                funny_embed = discord.Embed(colour=discord.Colour(0x4a6cac))

            funny_embed.set_image(url=gif_url)

            await full_msg.channel.send(embed=funny_embed)
        else:
            # No gif url in database for this command
            await full_msg.channel.send("No gif was found for this command.", delete_after=random.uniform(15.0, 20.0))
