class DatabaseInitializer:
    def run(self, db_connection):
        cursor = db_connection.cursor()

        print("Creating table classic_command")
        cursor.execute("CREATE TABLE \"classic_command\" ("
                       + "\"command\" TEXT NOT NULL, "
                       + "\"server_id\" TEXT NOT NULL, "
                       + "\"message\" TEXT NOT NULL, "
                       + "\"active\" INTEGER NOT NULL DEFAULT 1, "
                       + "\"description\" TEXT, "
                       + "\"nsfw\" INTEGER NOT NULL DEFAULT 0, "
                       + "PRIMARY KEY(\"server_id\",\"command\"));")

        print("Creating table classic_command_restriction")
        cursor.execute("CREATE TABLE \"classic_command_restriction\" ("
                       + "\"command\" TEXT NOT NULL, "
                       + "\"server_id\" TEXT NOT NULL, "
                       + "\"type\" TEXT NOT NULL, "
                       + "\"affected_id\" TEXT NOT NULL, "
                       + "PRIMARY KEY(\"command\",\"server_id\",\"type\",\"affected_id\"), "
                       + "FOREIGN KEY(\"command\",\"server_id\") "
                       + "REFERENCES \"classic_command\"(\"command\",\"server_id\"));")

        print("Creating table configuration")
        cursor.execute("CREATE TABLE \"configuration\" ("
                       + "\"key\" TEXT NOT NULL UNIQUE, "
                       + "\"value\" TEXT NOT NULL, "
                       + "PRIMARY KEY(\"key\"));")

        print("Creating table gif")
        cursor.execute("CREATE TABLE \"gif\" ("
                       + "\"command\" TEXT NOT NULL, "
                       + "\"url\" TEXT NOT NULL, "
                       + "\"active\" INTEGER NOT NULL, "
                       + "\"nsfw\" INTEGER NOT NULL DEFAULT 0, "
                       + "PRIMARY KEY(\"command\",\"url\"));")

        print("Creating table joke")
        cursor.execute("CREATE TABLE \"joke\" ("
                       + "\"heading\" TEXT, "
                       + "\"text\" TEXT NOT NULL UNIQUE, "
                       + "\"active\" INTEGER NOT NULL DEFAULT 1);")
