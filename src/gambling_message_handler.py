import random

import discord

from message_handler import MessageHandler


class GamblingMessageHandler(MessageHandler):
    def __init__(self, global_config, db_connection_wrapper):
        super().__init__(global_config, db_connection_wrapper)
        self.bot_name = global_config.bot_name

    rps_name = "rps"
    roulette_name = "roulette"

    @classmethod
    def can_handle(cls, full_msg, cmd_prefix):
        arg0 = cls._get_argument(full_msg, 0, cmd_prefix).lower()
        if arg0 == cls.rps_name or arg0 == cls.roulette_name:
            return True
        else:
            return False

    async def handle_message(self, full_msg):
        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix).lower()
        arg1 = self._get_argument(full_msg.content, 1, self.cmd_prefix).lower()

        if arg0 == self.rps_name:
            # Rock paper scissors

            if arg1 is None or len(arg1) <= 0:
                await full_msg.channel.send("You have to provide an argument for this command.", delete_after=21.0)
                await full_msg.delete(delay=21.0)
                return

            shapes = ["rock", "paper", "scissors"]
            if arg1 not in shapes:
                await full_msg.channel.send("You can only use rock, paper or scissors.")
                return
            else:
                # Convert shape to int
                arg1_int = {"rock": 0, "paper": 1, "scissors": 2}[arg1]

            # Select a shape for Katarina
            bot_shape = random.randint(0, 2)
            bot_shape_str = shapes[bot_shape]

            # Determine winner
            if arg1_int == bot_shape:
                winner = "Nobody"
            elif arg1_int == 0 and bot_shape == 2:
                winner = full_msg.author.display_name
            elif arg1_int == 1 and bot_shape == 0:
                winner = full_msg.author.display_name
            elif arg1_int == 2 and bot_shape == 1:
                winner = full_msg.author.display_name
            else:
                winner = self.bot_name

            # Result
            result_embed = discord.Embed(title="Rock paper scissors", colour=discord.Colour(0x398b18))
            result_embed.add_field(name=full_msg.author.display_name + "'s shape", value="`" + arg1 + "`")
            result_embed.add_field(name=self.bot_name + "'s shape", value="`" + bot_shape_str + "`")
            result_embed.add_field(name="Winner", value="`" + winner + "`")
            await full_msg.channel.send(embed=result_embed)

        elif arg0 == self.roulette_name:
            red = [32, 19, 21, 25, 34, 27, 36, 30, 23, 5, 16, 1, 14, 9, 18, 7, 12, 3]
            black = [15, 4, 2, 17, 6, 13, 11, 8, 10, 24, 33, 20, 31, 22, 29, 28, 35, 26]
            green = [0]

            # Determine result
            roulette_result = random.randint(0, 36)

            if roulette_result in red:
                result_color = discord.Colour(0xff0000)
                color_str = "`red`"
            elif roulette_result in black:
                result_color = discord.Colour(0x000000)
                color_str = "`black`"
            else:
                result_color = discord.Colour(0x0b5602)
                color_str = "`green`"

            if roulette_result % 2 == 0:
                parity = "`even`"
            else:
                parity = "`odd`"

            if roulette_result <= 18:
                section = "`low`"
            else:
                section = "`high`"

            # Result
            result_embed = discord.Embed(title="Roulette", colour=result_color,
                                         description="Outcome: **" + str(roulette_result) + "**")
            result_embed.add_field(name="Color", value=color_str)
            result_embed.add_field(name="Parity", value=parity)
            result_embed.add_field(name="Section", value=section)
            await full_msg.channel.send(embed=result_embed)
