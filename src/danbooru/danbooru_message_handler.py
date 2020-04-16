import discord

from danbooru.danbooru_client import DanbooruClient
from danbooru.e621_client import E621Client
from danbooru.gelbooru_client import GelbooruClient
from danbooru.konachan_client import KonachanClient
from danbooru.rule34_client import Rule34Client
from danbooru.safebooru_client import SafebooruClient
from danbooru.yandere_client import YandereClient
from message_handler import MessageHandler


class DanbooruMessageHandler(MessageHandler):
    def __init__(self, global_config, db_connection_wrapper):
        super().__init__(global_config, db_connection_wrapper)
        self.bot_version = global_config.bot_version

    async def _danbooru(self, explicit_only):
        danbooru_client = DanbooruClient(self.bot_version)
        return await danbooru_client.get_post_as_embed(explicit_only)

    async def _e621(self, explicit_only):
        e621_client = E621Client(self.bot_version)
        return await e621_client.get_post_as_embed(explicit_only)

    async def _gelbooru(self, explicit_only):
        gelbooru_client = GelbooruClient(self.bot_version)
        return await gelbooru_client.get_post_as_embed(explicit_only)

    async def _konachan(self, explicit_only):
        konachan_client = KonachanClient(self.bot_version)
        return await konachan_client.get_post_as_embed(explicit_only)

    async def _rule34(self, explicit_only):
        rule34_client = Rule34Client(self.bot_version)
        return await rule34_client.get_post_as_embed(explicit_only)

    async def _safebooru(self, explicit_only):
        safebooru_client = SafebooruClient(self.bot_version)
        return await safebooru_client.get_post_as_embed(explicit_only)

    async def _yandere(self, explicit_only):
        yandere_client = YandereClient(self.bot_version)
        return await yandere_client.get_post_as_embed(explicit_only)

    boards = {
        "danbooru": _danbooru,
        "e621": _e621,
        "gelbooru": _gelbooru,
        "konachan": _konachan,
        "rule34": _rule34,
        "safebooru": _safebooru,
        "yandere": _yandere
    }

    @classmethod
    def can_handle(cls, full_msg, cmd_prefix):
        arg0 = cls._get_argument(full_msg, 0, cmd_prefix).lower()
        if len(arg0) > 1:
            if arg0[-1] == "+":
                arg0 = arg0[:-1]
        if arg0 in cls.boards:
            return True
        else:
            return False

    async def handle_message(self, full_msg):
        if not isinstance(full_msg.channel, discord.TextChannel):
            # Only works with servers (because DM channels can't be marked as nsfw)
            return

        if not full_msg.channel.is_nsfw():
            await full_msg.channel.send("This command is not safe for work. You can only invoke it in a nsfw channel.",
                                        delete_after=18.0)
            await full_msg.delete(delay=18.0)
            return

        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix).lower()
        if arg0[-1] == "+":
            arg0 = arg0[:-1]
            explicit_only = True
        else:
            explicit_only = False

        # Redirect to the correct method
        embed_post = await self.boards[arg0](self, explicit_only)
        await full_msg.channel.send(embed=embed_post)
