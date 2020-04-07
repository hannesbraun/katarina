import sqlite3
import sys

from config_init import ConfigurationInitializer
from db_init import DatabaseInitializer
from gif_init import GifFactory

if __name__ == "__main__":
    if len(sys.argv) >= 3:
        # Get the database connection
        print("Connecting to database")
        connection = sqlite3.connect(sys.argv[2])
        connection.row_factory = sqlite3.Row

        if sys.argv[1].lower() == "init":
            db_initializer = DatabaseInitializer()
            db_initializer.run(connection)
            config_initializer = ConfigurationInitializer()
            config_initializer.run(connection)

        if sys.argv[1].lower() == "init" or sys.argv[1].lower() == "update":
            cursor = connection.cursor()
            cursor.execute("SELECT value FROM configuration WHERE key = 'db_version'")
            db_version = cursor.fetchone()[0]
            if db_version == "1":
                gif_factory = GifFactory()
                gif_factory.update_db(connection)
            else:
                print("Error: incompatible database version")

        # Commit
        print("Committing changes")
        connection.commit()
        print("Closing database")
        connection.close()
    else:
        print("Not enough arguments")
