# Katarina

Just another discord bot

*Yes, some features of this bot are heavily inspired by [Miki](https://miki.ai). Thank you, dear Miki developers.*

**Note:** this project is still very young and may contain bugs. If you find any of those, don't hesitate to submit them.

## Getting started
* Make sure you have Python 3.5.3 or higher installed (requirement for discord.py).
* Clone this repository.
* To install the required packages, change into the root directory of this repository and run
  ```bash 
  pip3 install -r requirements.txt
  ```
* Change into the `maintenance` directory and create the database file with:
  ```
  python3 . init /path/to/your/database.db
  ```
* Adjust the settings inside the database (table: configuration):
    + `bot_name`: the name of the bot (mainly used for the GIF stuff)
    + `owner_id`: the user id of the bot owner
    + `cmd_prefix`: the command prefix (default: `!`)
    + `tmp_dir`: the path to a directory to temporarily store downloads in (used for the music bot feature)
* Change to the `src` directory and run Katarina with:
  ```bash 
  python3 . /path/to/your/database.db <your access token>
  ```

## Available modules

Note: The following commands are documented with the prefix `!`. Depending on your configuration, this may vary.

### Random champion from League of Legends
* `!rlc [amount]`: Katarina will print a random champion name from League of Legends.
    + Amount is the number of champions Katarina will generate. (If it is not provided , it defaults to 1.)

### Classic command system

The classic command system basically lets you define your own commands. You can set what will be returned for a specific command. Built-in commands can't be overwritten.
Commands are only valid for the server they were set from.
 
* `!setcc <command> <action> <value>`: This sets a command with `<command>` being the targeted command. There are several possible values for `<action>`:
    + `m`: With `<value>`, the actual message that will be returned will be set. No need to escape.
    + `a`: With `<value>` being either `0` or `1`, the actual message will be set inactive or active. This defaults to `1`.
    + `nsfw`: With `<value>` being either `0` or `1`, the actual message will be set safe for work or not safe for work. This defaults to `0`. If set to not safe for work, the command will only work in text channels marked as not safe for work.
    + `d`: With `<value>`, the description for the command will be set. No need to escape.
    + `r`: This sets a restriction for the command. `<value>` splits up into two arguments with the first one being the restriction type and the second one being the affected id of the user or channel. Possible values for the restriction type are:
        - `uw`: User whitelist (The given user (id) will be whitelisted for the specified command.)
        - `ub`: User blacklist (The given user (id) will be blacklisted for the specified command.)
        - `cw`: Channel whitelist (The given channel (id) will be whitelisted for the specified command.)
        - `cb`: Channel blacklist (The given channel (id) will be blacklisted for the specified command.)
        
      If a whitelist restriction is active, the corresponding blacklist will be ignored.
    + `rmr`: This deletes a restriction for the command. `<value>` splits up into two arguments with the first one being the restriction type and the second one being the affected id of the user or channel.
* `!rmcc <command>`: This deletes a command together with all its restrictions.
* `!ccdata <command>`: This shows the configuration for a command without the message (to avoid problems with nsfw content).
* `!ccs-help`: A list of available commands together with their descriptions will be sent to you as a direct message.

### Having fun with GIFs

For these commands, random predefined gifs will be sent.
There are two categories of these commands: active and passive.
For active commands, you can add one or more mentions after the command. Passive commands will ignore this.

Available active commands:
* ``!bite``
* ``!cake``
* ``!cuddle``
* ``!glare``
* ``!highfive``
* ``!hug``
* ``!kiss``
* ``!lick``
* ``!pat``
* ``!poke``
* ``!punch``
* ``!slap``

Available passive commands:
* ``!confused``
* ``!cry``
* ``!lewd``
* ``!pout``
* ``!smug``
* ``!stare``

### More fun

* `!cat`: A random image of a cat will be sent.
* `!dog`: A random image of a dog will be sent.

### Music bot

Katarina can play music. Currently, YouTube is the only supported media source.
The bot can only be controlled while being in the same channel together with the bot.

* `!play <url>`: This command adds the media of the given url to the play queue. If the bot is currently not connected to a voice channel, it will connect automatically.
* `!pause`: This command pauses or resumes the playback based on the current state.
* `!stop`: This command clears the queue and stops the playback.
* `!clearqueue`: This command clears the queue. The current track will continue playing.
* `!skip`: This command skips the track which is currently playing.
* `!queue`: This command returns the next 19 tracks in the queue. (This may take some time because the track titles have to be retrieved first.)
* `!shuffle`: This command shuffles the queue.

### Math

You can perform some basic math operations.
Use the following syntax: `!math <number1> <operator> <number2>`

Supported operators are `+`, `-`, `*`, `/`, `%`.

Note: keep in mind that floating point arithmetic is used for these calculations. Some results may look weird if you're not familiar with this.

### Admin utilities

* `!clear <number of messages>`: Deletes the last `<number of messages>` messages in the current channel.
* `!mute <user> ...`: Mutes the mentioned users server-wide.
* `!slowmode`: Activates or deactivates the slowmode for the current channel. If activated, the delay will be set to 30 seconds.
* `!unmute <user> ...`: Unmutes the mentioned users server-wide. 

### Jokes

With `!joke`, Katarina will tell a joke. By default, no joke is present in the database. You can add jokes yourself by adding entries to the table `joke`.

### Access to Danbooru-like imageboards

With these commands, a random image from the requested imageboard will be sent. Appending a `+` to the command will only return images rated as explicit (e.g. `!danbooru+`).
Both versions of these commands only work in nsfw channels.

* `!danbooru`
* `!e621`
* `!gelbooru`
* `!konachan`
* `!rule34`
* `!safebooru`
* `!yandere`

### Miscellaneous
* `!katarina-source`: Katarina will send a link to this repository.
* `!about`: Karatina will print the her own current version and the license notice.

## Used libraries

* [discord.py](https://github.com/Rapptz/discord.py)
* [Requests](https://github.com/psf/requests)
* [pytube3](https://github.com/nficano/pytube)

## License

Katarina is licensed under the GNU Affero General Public License v3.0. For more information see [LICENSE](LICENSE "GNU AGPL v3.0").
