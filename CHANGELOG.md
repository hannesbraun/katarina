# Changelog

4.0.4 (January 7, 2021)
------------------------------
* New administration commands: `!mm` and `!permissions`
* Increased randomness for the Danbooru clients
* Dependency updates

4.0.3 (December 25, 2020)
------------------------------
* Dependency updates (makes SoundCloud work again with the music bot)
* Bugfix: the about command looks like it did in version 3 again

4.0.2 (December 17, 2020)
------------------------------
* New League of Legends champion for `rlc`: Rell
* Bugfix: executing the clear command with a very large number can no longer lead to a stack overflow

4.0.1 (December 5, 2020)
------------------------------
* Bugfix: executing `!clear` without an argument caused an unwanted exception
* Bugfix: fixed the output for `!about`

4.0.0 (December 4, 2020)
------------------------------
This is a major update, but the changes are mainly internal. Katarina got completely rewritten in Kotlin.
Well, so what's new?
* New supported sources for the music bot:
    + SoundCloud
    + Bandcamp
    + Twitch Streams
    + Vimeo
    + HTTP URLs
* Uptime command to see how long the bot is running already
* Faster access to Danbooru-like imageboards
* New command line arguments (should make maintenance hopefully easier)

3.1.7 (October 24, 2020)
------------------------------
* New League of Legends champion for `rlc`: Seraphine

3.1.6 (September 16, 2020)
------------------------------
* New League of Legends champion for `rlc`: Samira
* Increased the maximum amount of champions to generate

3.1.5 (September 7, 2020)
------------------------------
* Temporarily fix pytube
* Add the new League of Legends champions (Lillia and Yone)

3.1.4 (April 24, 2020)
------------------------------
* `rlc`: prevent generating a champion multiple times

3.1.3 (April 16, 2020)
------------------------------
* Danbooru: fixed a bug with too many tags
* Only allow access to Danbooru (and the other boards) within a server
* Fixed possible nsfw bugs with gif commands
* Now notifying users if an image url for a Danbooru post could not be found
* Performance optimization for accessing Danbooru-like boards

3.1.2 (April 13, 2020)
------------------------------
* Added handling for empty playlists in the music bot
* Fixed an error when providing private videos to the music bot
 
3.1.1 (April 10, 2020)
------------------------------
* Fixed a music bot bug where Katarina could pop from an empty queue

3.1.0 (April 10, 2020)
------------------------------
* Added  `cat` and `dog` commands

3.0.0 (April 8, 2020)
------------------------------
* Removed reactions to messages with a question mark
* Added a classic command system
* Added the ability to tell jokes
* Danbooru-like imageboards can now be accessed
* New gif commands
* Music bot *(YouTube only)*
* Small gambling stuff (Roulette and Rock Paper Scissors)
* Math command
* Admin commands

2.0.1 (March 21, 2020)
------------------------------
* Katarina now automatically reacts to messages with a question mark

2.0.0 (March 16, 2020)
------------------------------
*Note: Katarina is no longer using Go. Instead, she's using Python now.*

* Removed hardcoded soundboard
* Added the `rlc` command to generate random champions from League of Legends
* Now providing a link to the source code repository at GitHub with `katarina-source`

1.0.0 (July 27, 2019)
------------------------------
* Initial release
