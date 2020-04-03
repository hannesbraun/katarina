from misc.misc_message_handler import MiscMessageHandler
from rlc.rlc_message_handler import RlcMessageHandler


class StageOneParser:
    def __init__(self, configuration):
        self.configuration = configuration

    def get_message_handler(self, full_msg):
        if full_msg.content.startswith(self.configuration.cmd_prefix):
            msg = full_msg.content[len(self.configuration.cmd_prefix):]

            if RlcMessageHandler.can_handle(msg):
                # Random LoL Champion
                rlc_message_handler = RlcMessageHandler(self.configuration)
                return rlc_message_handler
            elif MiscMessageHandler.can_handle(msg):
                # Miscellaneous stuff
                misc_message_handler = MiscMessageHandler(self.configuration)
                return misc_message_handler
        else:
            # Natural language processing here
            return None
