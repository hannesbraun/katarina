import discord
import requests


class CatClient:
    def __init__(self, bot_version):
        self.bot_version = bot_version

    def _get_error_embed(self, error_message):
        return discord.Embed(title="Error", description=error_message, colour=discord.Colour(0xff001f))

    async def get_img_as_embed(self):
        # Get random cat
        try:
            headers = {
                "User-Agent": "Katarina " + self.bot_version
            }
            result = requests.get(url="https://aws.random.cat/meow", headers=headers).json()
        except ValueError:
            return self._get_error_embed("Decoding the json object failed.")

        # Create embed
        cat_embed = discord.Embed(title="Cat!", colour=discord.Colour(0xee44a3))
        cat_embed.set_image(url=result["file"])

        return cat_embed
