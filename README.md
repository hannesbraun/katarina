# Katarina

Just another discord bot

*Yes, some features of this bot are heavily inspired by [Miki](https://miki.ai). Thank you, dear Miki developers.*

**Note:** this project is still very young and may contain bugs. If you find any of those, don't hesitate to submit them.

## Getting started
Make sure you have Java 1.8 or higher installed on your system. 

To build Katarina, you need the JDK (Java Development Kit). If you only want to run Katarina, the JRE (Java Runtime Environment) is enough. I recommend using [AdoptOpenJDK](https://adoptopenjdk.net).

### Build
* Clone this repository.
* On Linux or macOS run
  ```bash 
  ./gradlew shadowJar
  ```
* You will find the generated JAR file inside the directory `build/libs/`. It is named something like `katarina-4.0.0-all.jar`.

### Run
Before running Katarina for the first time, it is recommended to first generate the database.
To do this, run something like
```bash
  java -jar katarina-4.0.0-all.jar -d katarina.db -m initGifs
 ```
A file called `katarina.db` (or whatever you named it) will be created. With an SQLite Editor, adjust the following settings inside the database to your needs (table: configuration):
* `bot_name`: the name of the bot (mainly used for the GIF stuff)
* `owner_id`: the user id of the bot owner
* `cmd_prefix`: the command prefix
  
After this initial setup, you can run Katarina with something like
```bash
  java -jar katarina-4.0.0-all.jar -d katarina.db -t <token>
 ```
You need to replace `<token>` with the token of your bot.

Shutdown is available from within Discord: send `!shutdown` to Katarina as a direct message. This only works, if you are the owner of the bot (specified in the configuration table of the database).

## Available modules/commands
Please refer to the [user guide](USER_GUIDE.md).

## License

Katarina is licensed under the GNU Affero General Public License v3.0. For more information see [LICENSE](LICENSE "GNU AGPL v3.0").
