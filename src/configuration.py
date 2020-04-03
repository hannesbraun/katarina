class Configuration:
    def __init__(self, db_connection_wrapper):
        db_connection_wrapper.lockConnection()

        cursor = db_connection_wrapper.connection.cursor()

        cursor.execute("SELECT value FROM configuration WHERE key = 'cmd_prefix'")
        self.cmd_prefix = cursor.fetchone()["value"]

        cursor.execute("SELECT value FROM configuration WHERE key = 'owner_id'")
        self.owner_id = int(cursor.fetchone()["value"])

        db_connection_wrapper.unlockConnection()