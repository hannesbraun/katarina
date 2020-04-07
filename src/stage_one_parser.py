from ccs.ccs_message_handler import CcsMessageHandler
from danbooru.danbooru_message_handler import DanbooruMessageHandler
from gambling_message_handler import GamblingMessageHandler
from gif.gif_message_handler import GifMessageHandler
from joke.joke_message_handler import JokeMessageHandler
from math_fun.math_message_handler import MathMessageHandler
from misc.misc_message_handler import MiscMessageHandler
from music.music_message_handler import MusicMessageHandler
from rlc.rlc_message_handler import RlcMessageHandler


class StageOneParser:
    def __init__(self, configuration, db_connection_wrapper):
        self.configuration = configuration
        self.db_connection_wrapper = db_connection_wrapper

    async def get_message_handler(self, full_msg):
        if full_msg.content.startswith(self.configuration.cmd_prefix):

            if RlcMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Random LoL Champion
                rlc_message_handler = RlcMessageHandler(self.configuration, self.db_connection_wrapper)
                return rlc_message_handler
            elif DanbooruMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Send image from danbooru-like board
                danbooru_message_handler = DanbooruMessageHandler(self.configuration, self.db_connection_wrapper)
                return danbooru_message_handler
            elif GifMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Send a gif
                gif_message_handler = GifMessageHandler(self.configuration, self.db_connection_wrapper)
                return gif_message_handler
            elif JokeMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Tell a joke
                joke_message_handler = JokeMessageHandler(self.configuration, self.db_connection_wrapper)
                return joke_message_handler
            elif MathMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Do some math
                math_message_handler = MathMessageHandler(self.configuration, self.db_connection_wrapper)
                return math_message_handler
            elif MusicMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Music bot
                music_message_handler = MusicMessageHandler(self.configuration, self.db_connection_wrapper)
                return music_message_handler
            elif MiscMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Miscellaneous stuff
                misc_message_handler = MiscMessageHandler(self.configuration, self.db_connection_wrapper)
                return misc_message_handler
            elif GamblingMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Gambling
                gambling_message_handler = GamblingMessageHandler(self.configuration, self.db_connection_wrapper)
                return gambling_message_handler
            elif (await CcsMessageHandler.can_handle(full_msg, self.db_connection_wrapper, self.configuration)):
                # Custom command system
                ccs_message_handler = CcsMessageHandler(self.configuration, self.db_connection_wrapper)
                return ccs_message_handler
        else:
            # Natural language processing here
            return None
