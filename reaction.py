import random

RND_EMOJIS = ["\U0001f498",  # Heart (with arrow)
              "\U0001f4a9",  # Pile of poo
              "\U0001f937",  # Shrugging
              "\U0001f428",  # Koala
              "\U0001f30b",  # Volcano
              "\U0001f9c2",  # Salt
              ]

def get_rand_reaction():
    mode = random.randint(0, 36)
    if mode != 0:
        if bool(random.getrandbits(1)):
            return "\U0001f44d"
        else:
            return "\U0001f44e"
    else:
        # Some emojis
        emoji_index = random.randint(0, len(RND_EMOJIS) - 1)
        return RND_EMOJIS[emoji_index]
