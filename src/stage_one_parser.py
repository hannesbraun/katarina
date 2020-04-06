from ccs.ccs_message_handler import CcsMessageHandler
from danbooru.danbooru_message_handler import DanbooruMessageHandler
from joke.joke_message_handler import JokeMessageHandler
from misc.misc_message_handler import MiscMessageHandler
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
            elif JokeMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Tell a joke
                joke_message_handler = JokeMessageHandler(self.configuration, self.db_connection_wrapper)
                return joke_message_handler
            elif MiscMessageHandler.can_handle(full_msg.content, self.configuration.cmd_prefix):
                # Miscellaneous stuff
                misc_message_handler = MiscMessageHandler(self.configuration, self.db_connection_wrapper)
                return misc_message_handler
            elif (await CcsMessageHandler.can_handle(full_msg, self.db_connection_wrapper, self.configuration)):
                # Custom command system
                ccs_message_handler = CcsMessageHandler(self.configuration, self.db_connection_wrapper)
                return ccs_message_handler
        else:
            # Natural language processing here
            return None
