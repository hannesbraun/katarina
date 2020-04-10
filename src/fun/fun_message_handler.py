from fun.cat_client import CatClient
from fun.dog_client import DogClient
from message_handler import MessageHandler


class FunMessageHandler(MessageHandler):
    def __init__(self, global_config, db_connection_wrapper):
        super().__init__(global_config, db_connection_wrapper)
        self.bot_version = global_config.bot_version

    async def _cat(self):
        cat_client = CatClient(self.bot_version)
        return await cat_client.get_img_as_embed()

    async def _dog(self):
        dog_client = DogClient(self.bot_version)
        return await dog_client.get_img_as_embed()

    boards = {
        "cat": _cat,
        "dog": _dog
    }

    @classmethod
    def can_handle(cls, full_msg, cmd_prefix):
        arg0 = cls._get_argument(full_msg, 0, cmd_prefix).lower()
        if arg0 in cls.boards:
            return True
        else:
            return False

    async def handle_message(self, full_msg):
        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix).lower()

        # Redirect to the correct method
        embed_post = await self.boards[arg0](self)
        await full_msg.channel.send(embed=embed_post)
