# User guide (available modules)

Note: The following commands are documented with the prefix `!`. Depending on your configuration, this may vary.

## Random champion from League of Legends
* `!rlc [amount]`: Katarina will print a random champion name from League of Legends.
    + Amount is the number of champions Katarina will generate. (If it is not provided , it defaults to 1.)

## Classic command system

The classic command system basically lets you define your own commands. You can set what will be returned for a specific command. Built-in commands can't be overwritten.
Commands are only valid for the server they were set from.

* `!setcc <command> <action> <value>`: This sets a command with `<command>` being the targeted command. There are several possible values for `<action>`:
    + `m`: With `<value>`, the actual message that will be returned will be set. No need to escape.
    + `a`: With `<value>` being either `0` or `1`, the actual message will be set inactive or active. This defaults to `1`.
    + `nsfw`: With `<value>` being either `0` or `1`, the actual message will be set safe for work or not safe for work. This defaults to `0`. If set to not safe for work, the command will only work in text channels marked as not safe for work.
    + `d`: With `<value>`, the description for the command will be set. No need to escape.
    + `r`: This sets a restriction for the command. `<value>` splits up into two arguments with the first one being the restriction type, and the second one being the affected id of the user or channel. Possible values for the restriction type are:
        - `uw`: User whitelist (The given user (id) will be whitelisted for the specified command.)
        - `ub`: User blacklist (The given user (id) will be blacklisted for the specified command.)
        - `cw`: Channel whitelist (The given channel (id) will be whitelisted for the specified command.)
        - `cb`: Channel blacklist (The given channel (id) will be blacklisted for the specified command.)

      If a whitelist restriction is active, the corresponding blacklist will be ignored.
    + `rmr`: This deletes a restriction for the command. `<value>` splits up into two arguments with the first one being the restriction type, and the second one being the affected id of the user or channel.
* `!rmcc <command>`: This deletes a command together with all its restrictions.
* `!ccdata <command>`: This shows the configuration for a command without the message (to avoid problems with nsfw content).
* `!ccs-help`: A list of available commands together with their descriptions will be sent to you as a direct message.

## Having fun with GIFs

For these commands, random predefined GIFs will be sent.
There are two categories of these commands: active and passive.
For active commands, you can add one or more mentions after the command. Passive commands will ignore this.

Available active commands:
* `!bite`
* `!cake`
* `!cuddle`
* `!glare`
* `!highfive`
* `!hug`
* `!kiss`
* `!lick`
* `!pat`
* `!poke`
* `!punch`
* `!slap`

Available passive commands:
* `!confused`
* `!cry`
* `!lewd`
* `!pout`
* `!smug`
* `!stare`

A note for the bot owners: you can adjust these GIFs by editing the table `gif` in your database.
If you want to add missing default GIFs, just run Katarina again in the `initGifs` mode.

## Random Animals

* `!cat`: A random image of a cat will be sent.
* `!dog`: A random image of a dog will be sent.

## Music bot

Katarina can play music. Currently, the following sources are supported:
* YouTube
* SoundCloud
* Bandcamp
* Twitch Streams
* Vimeo
* Monstercat
* HTTP URLs

The bot can only be controlled while being in the same channel together with the bot.

* `!play <url>`: This command adds the media of the given url to the play queue. If the bot is currently not connected to a voice channel, it will connect automatically.
* `!pause`: This command pauses or resumes the playback based on the current state.
* `!stop`: This command clears the queue and stops the playback.
* `!clearqueue`: This command clears the queue. The current track will continue playing.
* `!skip`: This command skips the track which is currently playing.
* `!queue`: This command returns the next 21 tracks in the queue.
* `!shuffle`: This command shuffles the queue.

## Math

You can perform some basic math operations.
Use the following syntax: `!math <number1> <operator> <number2>`

Supported operators are `+`, `-`, `*`, `/`, `%`.

Note: keep in mind that floating-point arithmetic is used for these calculations. Some results may look weird if you're not familiar with this.

## Admin utilities

* `!clear <number of messages>`: Deletes the last `<number of messages>` messages in the current channel.
* `!mm <source channel id> <destination channel id>`: Moves the connected members of the source channel to the destination channel.
* `!mute <user> ...`: Mutes the mentioned users server-wide.
+ `!permissions <user> <channel id>`: Shows the permissions of a user for the given channel.
* `!slowmode`: Activates or deactivates the slowmode for the current channel. If activated, the delay will be set to 30 seconds.
* `!unmute <user> ...`: Unmutes the mentioned users server-wide.

## Jokes

With `!joke`, Katarina will tell a joke. By default, no joke is present in the database.

A note for the bot owners: You can add jokes yourself by adding entries to the table `joke`.

## Gambling
* `!rps <shape>`: Play "Rock Paper Scissors" against the bot. Shape has to be one of the following values: `rock`, `paper`, `scissors`.
* `!roulette`: Play roulette (very basic)

## Access to Danbooru-like imageboards

With these commands, a random image from the requested imageboard will be sent. Appending a `+` to the command will only return images rated as explicit (e.g. `!danbooru+`).
Both versions of these commands only work in nsfw channels.

* `!danbooru`
* `!e621`
* `!gelbooru`
* `!konachan`
* `!rule34`
* `!safebooru`
* `!yandere`

## Miscellaneous/Meta
* `!katarina-source`: Katarina will send a link to this repository.
* `!about`: Katarina will print her own current version along with the license notice.
* `!uptime`: Katarina will print the uptime in days.
* `!help`: Katarina will print a link to this file.
