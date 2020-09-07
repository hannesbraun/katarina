import discord
import requests


class DogClient:
    def __init__(self, bot_version):
        self.bot_version = bot_version

    def _get_error_embed(self, error_message):
        return discord.Embed(title="Error", description=error_message, colour=discord.Colour(0xff001f))

    async def get_img_as_embed(self):
        # Get random dog
        try:
            headers = {
                "User-Agent": "Katarina " + self.bot_version
            }
            result = requests.get(url="https://random.dog/woof.json", headers=headers).json()
        except ValueError:
            return self._get_error_embed("Decoding the json object failed.")

        # Create embed
        dog_embed = discord.Embed(title="Dog!", colour=discord.Colour(0x44eeb2))
        dog_embed.set_image(url=result["url"])

        return dog_embed
