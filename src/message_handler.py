from abc import ABCMeta, abstractmethod


class MessageHandler:
    __metaclass__ = ABCMeta

    def __init__(self, global_config):
        self.cmd_prefix = global_config.cmd_prefix

    @classmethod
    @abstractmethod
    def can_handle(cls, msg):
        pass

    @abstractmethod
    async def handle_message(self, full_msg):
        pass

    def set_db(self, db_connection_wrapper):
        self.db_connection_wrapper = db_connection_wrapper
