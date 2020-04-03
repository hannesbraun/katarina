import sqlite3
import threading


class DatabaseConnectionWrapper:
    def __init__(self, db_file):
        self.lock = threading.Lock()
        self.connection = sqlite3.connect(db_file)
        self.connection.row_factory = sqlite3.Row

    def lockConnection(self):
        self.lock.acquire(blocking=True)

    def unlockConnection(self):
        self.lock.release()

    def shutdown(self):
        self.lockConnection()
        self.connection.close()
