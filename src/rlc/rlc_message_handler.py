from message_handler import MessageHandler
from rlc.rlc_generator import RlcGenerator


# Returns one or more random champions from League of Legends
class RlcMessageHandler(MessageHandler):
    name = "rlc"

    def __init__(self, global_config):
        self.cmd_prefix = global_config.cmd_prefix

    @classmethod
    def can_handle(cls, msg):
        if msg.lower() == cls.name or msg.lower().startswith(cls.name + " "):
            return True
        else:
            return False

    def _extract_amount_argument(self, raw_msg):
        amount = 1

        if len(raw_msg) > len(self.name):
            # Check if number supplied for generating multiple champions
            if raw_msg[len(self.name)] == " ":
                index_argument_end = raw_msg[len(self.name) + 1:].find(" ")
                if index_argument_end == -1:
                    # Something else follows after the argument
                    str_argument = raw_msg[4:]
                else:
                    # Nothing follows after the amount argument
                    str_argument = raw_msg[4:index_argument_end]

                # Try converting the argument to an int
                try:
                    print(str_argument)
                    amount = int(str_argument)
                except ValueError:
                    pass

        return amount

    async def handle_message(self, full_msg):
        raw_msg = full_msg.content[len(self.cmd_prefix):]

        # Amount of champions to generate
        amount = self._extract_amount_argument(raw_msg)
        if amount < 1:
            amount = 1
        elif amount > 10:
            amount = 10

        rlc_generator = RlcGenerator()

        # Generate the champion(s)
        champions = []
        for i in range(amount):
            champions.append(rlc_generator.generate())

        # Send message (champions separated by new line)
        await full_msg.channel.send("\n".join(champions))
