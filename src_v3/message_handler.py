from abc import ABCMeta, abstractmethod


class MessageHandler:
    __metaclass__ = ABCMeta

    def __init__(self, global_config, db_connection_wrapper):
        self.cmd_prefix = global_config.cmd_prefix
        self.db_connection_wrapper = db_connection_wrapper

    @staticmethod
    def _get_argument(raw_msg, index, cmd_prefix):
        tmp_msg = raw_msg[len(cmd_prefix):]
        arg_list = tmp_msg.split()

        if index >= len(arg_list):
            return ""
        else:
            return arg_list[index]

    @abstractmethod
    async def handle_message(self, full_msg):
        pass
