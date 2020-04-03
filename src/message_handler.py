from abc import ABCMeta, abstractmethod


class MessageHandler:
    __metaclass__ = ABCMeta

    @classmethod
    @abstractmethod
    def can_handle(cls, msg):
        pass

    @abstractmethod
    async def handle_message(self, full_msg):
        pass

    def set_db(self, db_connection_wrapper):
        self.db_connection_wrapper = db_connection_wrapper
