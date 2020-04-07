class ConfigurationInitializer:
    def run(self, db_connection):
        cursor = db_connection.cursor()

        print("Initializing the configuration")
        cursor.execute("INSERT INTO configuration (key, value) VALUES('db_version', '1')")
        cursor.execute("INSERT INTO configuration (key, value) VALUES('cmd_prefix', '!')")
        cursor.execute("INSERT INTO configuration (key, value) VALUES('bot_name', 'Katarina')")
        cursor.execute("INSERT INTO configuration (key, value) VALUES('owner_id', '0')")
        cursor.execute("INSERT INTO configuration (key, value) VALUES('tmp_dir', 'tmp')")
