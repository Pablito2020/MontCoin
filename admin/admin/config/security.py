from configparser import ConfigParser
from typing import Optional

from admin.config.config import ConfigurationOption

SECURITY_KEY = "Security"

PRIVATE_KEY_PATH = "users_private_key_path"
OPERATIONS_PRIVATE_KEY_PATH = "operations_private_key_path"
SIGN_ALGORITHM = "sign_algorithm"
NTP_SERVER = "ntp_server"


class UsersPrivateKeyPath(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> Optional[str]:
        return configuration.get(SECURITY_KEY, PRIVATE_KEY_PATH)


class OperationsPrivateKeyPath(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> Optional[str]:
        return configuration.get(SECURITY_KEY, OPERATIONS_PRIVATE_KEY_PATH)


class SignAlgorithm(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> Optional[str]:
        return configuration.get(SECURITY_KEY, SIGN_ALGORITHM)


class NtpServer(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> Optional[str]:
        return configuration.get(SECURITY_KEY, NTP_SERVER)
