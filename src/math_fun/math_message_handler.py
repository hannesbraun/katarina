import math

from message_handler import MessageHandler


class MathMessageHandler(MessageHandler):
    name = "math"

    @classmethod
    def can_handle(cls, raw_msg, cmd_prefix):
        if cls._get_argument(raw_msg, 0, cmd_prefix).lower() == cls.name:
            return True
        else:
            return False

    async def handle_message(self, full_msg):
        arg1 = self._get_argument(full_msg.content, 1, self.cmd_prefix)
        arg2 = self._get_argument(full_msg.content, 2, self.cmd_prefix)
        arg3 = self._get_argument(full_msg.content, 3, self.cmd_prefix)

        # Convert to number
        try:
            arg1_float = float(arg1)
        except ValueError:
            await full_msg.channel.send(arg1 + " is not a valid number.", delete_after=42.0)
            return
        try:
            arg3_float = float(arg3)
        except ValueError:
            await full_msg.channel.send(arg3 + " is not a valid number.", delete_after=42.0)
            return

        if arg2 == "+":
            # Add
            result = arg1_float + arg3_float
        elif arg2 == "-":
            # Subtract
            result = arg1_float - arg3_float
        elif arg2 == "*":
            # Multipy
            result = arg1_float * arg3_float
        elif arg2 == "/":
            # Divide
            if arg3_float == 0.0:
                await full_msg.channel.send("Remember: division by zero is not possible.")
                return
            result = arg1_float / arg3_float
        elif arg2 == "%":
            # Modulo
            if arg3_float == 0.0:
                await full_msg.channel.send("Remember: division by zero is not possible.")
                return
            result = arg1_float % arg3_float
        else:
            await full_msg.channel.send(arg2 + " is not a valid (supported) operator.", delete_after=42.0)
            return

        if "." not in arg1 and "." not in arg3 and arg2 != "/" and arg2 != "%":
            # Int is sufficient
            await full_msg.channel.send("Result: **" + str(math.trunc(result)) + "**")
        else:
            await full_msg.channel.send("Result: **" + str(result) + "**")
