import asyncio
import random

import discord
import requests


class DanbooruClient:
    def __init__(self, bot_version):
        self.bot_version = bot_version

    def _get_index_params(self):
        page = random.randint(1, 1000)
        return {"limit": "200", "page": str(page)}

    _title = "Danbooru"
    _index_url = "https://danbooru.donmai.us/posts.json"

    def _get_error_embed(self, error_message):
        return discord.Embed(title="Error", description=error_message, colour=discord.Colour(0xff001f))

    def _is_explicit(self, post):
        rating = post["rating"]
        if rating is None or len(rating) <= 0:
            return False

        if rating.lower() == "e":
            return True
        else:
            return False

    def _get_index_list(self, raw_index):
        return raw_index

    def _get_image_url(self, post):
        return post["file_url"]

    def _get_score(self, post):
        return post["score"]

    def _get_full_post_url(self, post):
        return "https://danbooru.donmai.us/posts/" + str(post["id"])

    def _get_tag_list(self, post):
        return post["tag_string"].split()

    def _get_tags(self, post):
        tags = self._get_tag_list(post)

        if len(tags) > 0:
            while True:
                tag_str = "`" + "`, `".join(tags) + "`"
                if len(tag_str) < 1024:
                    return tag_str
                elif len(tags) > 1:
                    # Tag string is too long: shorten it
                    tags = tags[:-1]
                else:
                    return "*A tag exceeds the max. field size of Discord embeds.*"
        else:
            return "*No tags available*"

    async def get_post_as_embed(self, explicit_only, try_nr=1):
        # Get index
        try:
            headers = {
                "User-Agent": "Katarina " + self.bot_version
            }
            index = self._get_index_list(requests.get(url=self._index_url, params=self._get_index_params(),
                                                      headers=headers).json())
        except ValueError:
            return self._get_error_embed("Decoding the json object failed.")

        if explicit_only:
            # Filter index
            index = list(filter(self._is_explicit, index))

        if len(index) <= 0:
            # No post in list: try again
            if try_nr < 10:
                await asyncio.sleep(1)
                return await self.get_post_as_embed(explicit_only, try_nr=try_nr + 1)
            else:
                return self._get_error_embed("No post found within 7 tries.")

        # Choose random post
        post = random.choice(index)

        try:
            img_url = self._get_image_url(post)
        except KeyError:
            return self._get_error_embed("No image url found for this post.")

        # Create embed
        danbooru_embed = discord.Embed(title=self._title, colour=discord.Colour(0xd480ff))
        danbooru_embed.set_image(url=img_url)
        danbooru_embed.add_field(name="\U0001f5d2 Tags", value=self._get_tags(post))
        danbooru_embed.add_field(name="\U0001f4c8 Score", value=str(self._get_score(post)))
        danbooru_embed.add_field(name="\U0001f4ce Full post", value="[Click here](" + str(self._get_full_post_url(post))
                                                                    + ")")

        return danbooru_embed
