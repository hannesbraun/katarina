from message_handler import MessageHandler
from rlc.rlc_generator import RlcGenerator


# Returns one or more random champions from League of Legends
class RlcMessageHandler(MessageHandler):
    name = "rlc"

    @classmethod
    def can_handle(cls, raw_msg, cmd_prefix):
        if cls._get_argument(raw_msg, 0, cmd_prefix).lower() == cls.name:
            return True
        else:
            return False

    def _extract_amount_argument(self, raw_msg):
        # Default amount
        amount = 1

        amount_str = self._get_argument(raw_msg, 1, "")

        # Try converting the argument to an int
        try:
            amount = int(amount_str)
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
