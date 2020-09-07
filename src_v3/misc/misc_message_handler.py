from message_handler import MessageHandler


class MiscMessageHandler(MessageHandler):
    def __init__(self, global_config, db_connection_wrapper):
        super().__init__(global_config, db_connection_wrapper)
        self.bot_version = global_config.bot_version

    commands = {
        "about": "?",
        "katarina-source": "Have a look at the code I'm made of: https://github.com/hannesbraun/katarina"
    }

    @classmethod
    def can_handle(cls, raw_msg, cmd_prefix):
        if cls._get_argument(raw_msg, 0, cmd_prefix).lower() in cls.commands:
            return True
        else:
            return False

    async def handle_message(self, full_msg):
        command = self._get_argument(full_msg.content, 0, self.cmd_prefix).lower()

        if command == "about":
            message = "Katarina version " + self.bot_version\
                      + "\nCopyright \u00a9 2020 Hannes Braun"\
                      + "\n\nThis program is free software: you can redistribute it and/or modify "\
                      + "it under the terms of the GNU Affero General Public License as published "\
                      + "by the Free Software Foundation, either version 3 of the License, or "\
                      + "(at your option) any later version."\
                      + "\n\nThis program is distributed in the hope that it will be useful, "\
                      + "but WITHOUT ANY WARRANTY; without even the implied warranty of "\
                      + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the "\
                      + "GNU Affero General Public License for more details: "\
                      + "https://www.gnu.org/licenses/agpl-3.0-standalone.html"
        else:
            message = self.commands[command]
        await full_msg.channel.send(message)
