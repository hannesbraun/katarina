import sqlite3
import asyncio


class DatabaseConnectionWrapper:
    def __init__(self, db_file):
        self.lock = asyncio.Lock()
        self.connection = sqlite3.connect(db_file)
        self.connection.row_factory = sqlite3.Row

    def shutdown(self):
        self.connection.close()
