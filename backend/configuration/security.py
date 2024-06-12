from configparser import ConfigParser
from typing import Optional

from configuration.config import ConfigurationOption

SECURITY_KEY = "Security"


class UsersPublicKeyPath(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> Optional[str]:
        return configuration.get(SECURITY_KEY, "users_public_key_path")


class OperationsPublicKeyPath(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> Optional[str]:
        return configuration.get(SECURITY_KEY, "operations_public_key_path")
