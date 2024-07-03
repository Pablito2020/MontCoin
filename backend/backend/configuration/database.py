import logging
from configparser import ConfigParser
from pathlib import Path

from backend.configuration.config import ConfigurationOption, Configuration

DB_KEY = "Database"

PRODUCTION = "production"
SQLITE_PATH = "sqlite_path"
POSTGRES_USER = "postgres_user"
POSTGRES_PASSWORD = "postgres_password"
POSTGRES_HOST = "postgres_host"
POSTGRES_PORT = "postgres_port"
POSTGRES_DB_NAME = "postgres_db_name"


class Production(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> bool:
        return configuration.getboolean(DB_KEY, PRODUCTION)


# sqlite database
class SQLitePath(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> str | None:
        return configuration.get(DB_KEY, SQLITE_PATH)


# postgres database

class PostgresUser(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> str | None:
        return configuration.get(DB_KEY, POSTGRES_USER)


class PostgresPassword(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> str | None:
        return configuration.get(DB_KEY, POSTGRES_PASSWORD)


class PostgresHost(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> str | None:
        return configuration.get(DB_KEY, POSTGRES_HOST)


class PostgresPort(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> str | None:
        return configuration.get(DB_KEY, POSTGRES_PORT)


class PostgresDbName(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> str | None:
        return configuration.get(DB_KEY, POSTGRES_DB_NAME)


def get_development_database_url() -> str:
    sqlite_path = Configuration.get(SQLitePath)
    assert sqlite_path is not None, "SQLite database path not found in configuration"
    if not Path(sqlite_path).exists():
        logging.info(f"SQLite database does not exist. Creating one...")
    return f"sqlite:///{sqlite_path}"


def get_production_database_url() -> str:
    user = Configuration.get(PostgresUser)
    assert user is not None, "Postgres user not found in configuration"
    password = Configuration.get(PostgresPassword)
    assert password is not None, "Postgres password not found in configuration"
    host = Configuration.get(PostgresHost)
    assert host is not None, "Postgres host not found in configuration"
    port = Configuration.get(PostgresDbName)
    assert port is not None, "Postgres port not found in configuration"
    db_name = Configuration.get(PostgresDbName)
    assert db_name is not None, "Postgres database name not found in configuration"
    return f"postgresql+psycopg2://{user}:{password}@{host}:{port}/{db_name}"


def get_database_url() -> str:
    prod = Configuration.get(Production)
    if prod is None:
        raise ValueError("Database production (true or false) is not configured on the config")
    return get_production_database_url() if prod else get_development_database_url()
