class Configuration:
    def __init__(self, db_connection_wrapper):
        self.bot_version = "3.0.0"

        cursor = db_connection_wrapper.connection.cursor()

        cursor.execute("SELECT value FROM configuration WHERE key = 'db_version'")
        self.db_version = cursor.fetchone()["value"]

        cursor.execute("SELECT value FROM configuration WHERE key = 'cmd_prefix'")
        self.cmd_prefix = cursor.fetchone()["value"]

        cursor.execute("SELECT value FROM configuration WHERE key = 'owner_id'")
        self.owner_id = int(cursor.fetchone()["value"])

        cursor.execute("SELECT value FROM configuration WHERE key = 'bot_name'")
        self.bot_name = cursor.fetchone()["value"]
