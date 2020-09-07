import asyncio
import os
import random

# The below imports are required by the patch
import json
from urllib.parse import parse_qs, unquote

# This function is based off on the changes made in
# https://github.com/nficano/pytube/pull/643

def apply_descrambler(stream_data, key):
    """Apply various in-place transforms to YouTube's media stream data.
    Creates a ``list`` of dictionaries by string splitting on commas, then
    taking each list item, parsing it as a query string, converting it to a
    ``dict`` and unquoting the value.
    :param dict stream_data:
        Dictionary containing query string encoded values.
    :param str key:
        Name of the key in dictionary.
    **Example**:
    >>> d = {'foo': 'bar=1&var=test,em=5&t=url%20encoded'}
    >>> apply_descrambler(d, 'foo')
    >>> print(d)
    {'foo': [{'bar': '1', 'var': 'test'}, {'em': '5', 't': 'url encoded'}]}
    """
    otf_type = "FORMAT_STREAM_TYPE_OTF"

    if key == "url_encoded_fmt_stream_map" and not stream_data.get(
        "url_encoded_fmt_stream_map"
    ):
        formats = json.loads(stream_data["player_response"])["streamingData"]["formats"]
        formats.extend(
            json.loads(stream_data["player_response"])["streamingData"][
                "adaptiveFormats"
            ]
        )
        try:
            stream_data[key] = [
                {
                    "url": format_item["url"],
                    "type": format_item["mimeType"],
                    "quality": format_item["quality"],
                    "itag": format_item["itag"],
                    "bitrate": format_item.get("bitrate"),
                    "is_otf": (format_item.get("type") == otf_type),
                }
                for format_item in formats
            ]
        except KeyError:
            cipher_url = []
            for data in formats:
                cipher = data.get("cipher") or data["signatureCipher"]
                cipher_url.append(parse_qs(cipher))
            stream_data[key] = [
                {
                    "url": cipher_url[i]["url"][0],
                    "s": cipher_url[i]["s"][0],
                    "type": format_item["mimeType"],
                    "quality": format_item["quality"],
                    "itag": format_item["itag"],
                    "bitrate": format_item.get("bitrate"),
                    "is_otf": (format_item.get("type") == otf_type),
                }
                for i, format_item in enumerate(formats)
            ]
    else:
        stream_data[key] = [
            {k: unquote(v) for k, v in parse_qsl(i)}
            for i in stream_data[key].split(",")
        ]

import discord
import pytube
pytube.__main__.apply_descrambler = apply_descrambler

from message_handler import MessageHandler


class MusicMessageHandler(MessageHandler):
    def __init__(self, global_config, db_connection_wrapper):
        super().__init__(global_config, db_connection_wrapper)
        self.tmp_dir = global_config.tmp_dir

    # Avaliable commands
    commands = ["play", "pause", "stop", "clearqueue", "skip", "queue", "shuffle"]

    _instances = {}
    _instances_lock = asyncio.Lock()
    _download_lock = asyncio.Lock()

    @classmethod
    def can_handle(cls, raw_msg, cmd_prefix):
        if cls._get_argument(raw_msg, 0, cmd_prefix).lower() in cls.commands:
            return True
        else:
            return False

    def _is_in_same_channel(self, full_msg):
        # Not writing to _instances: no need to lock
        user_channel = full_msg.author.voice.channel
        try:
            bot_channel = self.__class__._instances[full_msg.channel.guild.id]["client"].channel.id
        except AttributeError:
            return False

        if user_channel is not None and user_channel.id == bot_channel:
            return True
        else:
            return False

    async def _play(self, full_msg):
        # Is user in a voice channel?
        if full_msg.author.voice.channel is None:
            await full_msg.channel.send("Please connect to a voice channel to use this feature.", delete_after=120.0)
            await full_msg.delete(delay=120.0)
            return

        arg1 = self._get_argument(full_msg.content, 1, self.cmd_prefix)
        if len(arg1) <= 0:
            # No url provided
            await full_msg.channel.send("Error: please provide an url.", delete_after=120.0)
            await full_msg.delete(delay=120.0)
            return

        else:
            # Validate provided url
            valid_url = True
            no_content = False
            if not arg1.startswith(("https://www.youtube.com/", "www.youtube.com/", "youtube.com/",
                                    "https://youtu.be/", "youtu.be/")):
                # Does not seem to be a valid youtube url
                valid_url = False

            else:
                try:
                    # Is video?
                    pytube.YouTube(arg1)
                    is_playlist = False
                except pytube.exceptions.ExtractError:
                    try:
                        # Is playlist?
                        playlist = pytube.Playlist(arg1)
                        is_playlist = True
                        if len(playlist) <= 0:
                            no_content = True
                    except KeyError:
                        valid_url = False

            if not valid_url:
                # Invalid url, cancel operation
                await full_msg.channel.send("Error: not a valid url.", delete_after=120.0)
                await full_msg.delete(delay=120.0)
                return
            elif no_content:
                # Nothing found in playlist
                await full_msg.channel.send("Error: no media found in playlist. Is this maybe a private playlist?",
                                            delete_after=120.0)
                await full_msg.delete(delay=120.0)
                return

        async with self.__class__._instances_lock:
            if full_msg.channel.guild.id in self.__class__._instances:
                # Get existing instance
                instance = self.__class__._instances[full_msg.channel.guild.id]
                main_loop = False
            else:
                # Create new instance
                wait_event = asyncio.Event()

                def track_finished(error):
                    nonlocal wait_event
                    wait_event.set()

                instance = {
                    "event": wait_event,
                    "queue": [],
                    "client": None
                }
                self.__class__._instances[full_msg.channel.guild.id] = instance
                main_loop = True

            if main_loop or self._is_in_same_channel(full_msg):
                in_same_channel = True

                # Add to queue
                if not is_playlist:
                    instance["queue"].append(arg1)
                else:
                    for video in playlist:
                        instance["queue"].append(video)
            else:
                in_same_channel = False

        if in_same_channel:
            await full_msg.channel.send("Successfully added to queue.", delete_after=120.0)
            await full_msg.delete(delay=120.0)
        else:
            await full_msg.channel.send("You can only control the music bot from within the channel it is playing in.",
                                        delete_after=120.0)
            await full_msg.delete(delay=120.0)

        if not main_loop:
            # Bot is already playing and url is appended to queue
            return

        # Main playing loop starts here

        download_success = True

        # Connect to voice channel
        try:
            voiceclient = await full_msg.author.voice.channel.connect(timeout=20.0)
            async with self.__class__._instances_lock:
                instance["client"] = voiceclient
        except asyncio.TimeoutError:
            await full_msg.channel.send("Error: can't connect to voice channel.", delete_after=120.0)
            async with self.__class__._instances_lock:
                del self.__class__._instances[full_msg.channel.guild.id]
                return

        while True:
            # Get url
            async with self.__class__._instances_lock:
                if len(instance["queue"]) <= 0:
                    # Nothing more to play: disconnect and delete instance
                    await voiceclient.disconnect()
                    del self.__class__._instances[full_msg.channel.guild.id]
                    break
                else:
                    url = instance["queue"].pop(0)

            async with self.__class__._download_lock:
                # Get unused filename
                while True:
                    file_id = str(random.randint(0, 100000000000000000000000))
                    if not os.path.exists(file_id):
                        break

                if not wait_event.is_set():
                    try:
                        video = pytube.YouTube(url)
                    except pytube.exceptions.ExtractError:
                        download_success = False
                    else:
                        stream = video.streams.get_audio_only()
                        if stream.filesize_approx > 2000000000:
                            download_success = False
                        else:
                            download_path = stream.download(output_path=self.tmp_dir, filename=file_id)
                            download_success = True

            if download_success:
                # Play track
                voiceclient.play(discord.FFmpegOpusAudio(download_path), after=track_finished)
                await full_msg.channel.send("**Now playing**: " + stream.title, delete_after=float(video.length))
                await wait_event.wait()
                wait_event.clear()

                # File played: remove it
                os.remove(download_path)
            elif not wait_event.is_set():
                # Download not successful
                await full_msg.channel.send("Error while downloading the next track.", delete_after=120.0)
            # Else: skipped track before download

    async def _base_check(self, full_msg):
        async with self.__class__._instances_lock:
            if full_msg.channel.guild.id in self.__class__._instances:
                # Bot is connected to a voice channel on this server
                instance = self.__class__._instances[full_msg.channel.guild.id]
            else:
                instance = None

        if instance is None:
            await full_msg.channel.send("The bot is not connected to a voice channel.", delete_after=120.0)
            await full_msg.delete(delay=120.0)
            return False

        if not self._is_in_same_channel(full_msg):
            await full_msg.channel.send("You can only control the music bot from within the channel it is playing in.",
                                        delete_after=120.0)
            await full_msg.delete(delay=120.0)
            return False
        else:
            return True

    async def _shuffle(self, full_msg):
        base_check_result = await self._base_check(full_msg)
        if not base_check_result:
            return

        # Shuffle the queue
        async with self.__class__._instances_lock:
            random.shuffle(self.__class__._instances[full_msg.channel.guild.id]["queue"])
        await full_msg.channel.send("Successfully shuffled the queue.", delete_after=120.0)
        await full_msg.delete(delay=120.0)

    async def _stop(self, full_msg):
        base_check_result = await self._base_check(full_msg)
        if not base_check_result:
            return

        # Stop instance
        async with self.__class__._instances_lock:
            # Clear the queue to trigger disconnecting after stopping
            self.__class__._instances[full_msg.channel.guild.id]["queue"].clear()
            try:
                self.__class__._instances[full_msg.channel.guild.id]["client"].stop()
            except AttributeError:
                # Client not connected yet
                pass
        await full_msg.delete(delay=120.0)

    async def _clear(self, full_msg):
        base_check_result = await self._base_check(full_msg)
        if not base_check_result:
            return

        # Clear queue
        async with self.__class__._instances_lock:
            # Clear the queue
            self.__class__._instances[full_msg.channel.guild.id]["queue"].clear()
        await full_msg.channel.send("The queue is empty now.", delete_after=120.0)
        await full_msg.delete(delay=120.0)

    async def _pause(self, full_msg):
        base_check_result = await self._base_check(full_msg)
        if not base_check_result:
            return

        # Pause playback
        async with self.__class__._instances_lock:
            voiceclient = self.__class__._instances[full_msg.channel.guild.id]["client"]
            try:
                if voiceclient.is_playing():
                    voiceclient.pause()
                elif voiceclient.is_paused():
                    voiceclient.resume()
            except AttributeError:
                # Client not connected yet
                pass

        await full_msg.delete(delay=120.0)

    async def _skip(self, full_msg):
        base_check_result = await self._base_check(full_msg)
        if not base_check_result:
            return

        # Skip current track
        async with self.__class__._instances_lock:
            try:
                self.__class__._instances[full_msg.channel.guild.id]["client"].stop()
            except AttributeError:
                # Client not connected yet
                pass
        await full_msg.delete(delay=120.0)

    async def _queue(self, full_msg):
        base_check_result = await self._base_check(full_msg)
        if not base_check_result:
            return

        # Get the queue
        async with self.__class__._instances_lock:
            queue = self.__class__._instances[full_msg.channel.guild.id]["queue"]

        # Notify the users that this may take some time
        send_note = asyncio.create_task(
            full_msg.channel.send("Generating the queue message. This may take a few moments. Please be patient.",
                                  delete_after=20.0))

        # Limit to next 19 tracks (max length of youtube title: 100 characters)
        if len(queue) > 19:
            queue = queue[:19]

        message_str = ""
        i = 1
        for url in queue:
            # Get the title
            try:
                message_str += "\n" + str(i) + ". " + pytube.YouTube(url).streams.get_audio_only().title
            except pytube.exceptions.ExtractError:
                pass
            i += 1

            # To not disturb the current audio playing
            await asyncio.sleep(0.2)

        if len(message_str) <= 0:
            message_str = "*Queue is empty.*"

        await send_note
        await full_msg.channel.send(message_str, delete_after=600.0)
        await full_msg.delete(delay=120.0)

    async def handle_message(self, full_msg):
        if not isinstance(full_msg.channel, discord.TextChannel):
            # Only for servers
            return

        arg0 = self._get_argument(full_msg.content, 0, self.cmd_prefix).lower()

        if arg0 == "play":
            await self._play(full_msg)
        elif arg0 == "pause":
            await self._pause(full_msg)
        elif arg0 == "stop":
            await self._stop(full_msg)
        elif arg0 == "clearqueue":
            await self._clear(full_msg)
        elif arg0 == "queue":
            await self._queue(full_msg)
        elif arg0 == "skip":
            await self._skip(full_msg)
        elif arg0 == "shuffle":
            await self._shuffle(full_msg)
