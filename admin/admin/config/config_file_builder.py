import configparser
from dataclasses import dataclass

from admin.config.api import API_KEY, URL
from admin.config.security import SECURITY_KEY, PRIVATE_KEY_PATH, SIGN_ALGORITHM, NTP_SERVER

DEFAULT_SIGNATURE_ALGORITHM = "RS256"
DEFAULT_NTP_SERVER = "es.pool.ntp.org"


@dataclass
class ConfigFileBuilder:
    config = configparser.ConfigParser()

    def __add_section(self, section: str):
        if not self.config.has_section(section):
            self.config.add_section(section)

    def add_api_url(self, api_url: str) -> "ConfigFileBuilder":
        self.__add_section(API_KEY)
        self.config.set(API_KEY, URL, api_url)
        return self

    def add_user_private_key_path(
            self, user_private_key_path: str
    ) -> "ConfigFileBuilder":
        self.__add_section(SECURITY_KEY)
        self.config.set(SECURITY_KEY, PRIVATE_KEY_PATH, user_private_key_path)
        return self

    def add_signature_algorithm(self, signature_alg: str) -> "ConfigFileBuilder":
        self.__add_section(SECURITY_KEY)
        self.config.set(SECURITY_KEY, SIGN_ALGORITHM, signature_alg)
        return self

    def add_ntp_server(self, ntp_server: str) -> "ConfigFileBuilder":
        self.__add_section(SECURITY_KEY)
        self.config.set(SECURITY_KEY, NTP_SERVER, ntp_server)
        return self

    def write_file(self, file_path: str):
        if not self.is_correct():
            raise ValueError("The configuration is not correct. It must have an API URL and a private key path.")
        self.configure_optional_params()
        with open(file_path, "w") as configfile:
            self.config.write(configfile)

    def configure_optional_params(self):
        if not self.config.has_option(SECURITY_KEY, SIGN_ALGORITHM):
            self.add_signature_algorithm(DEFAULT_SIGNATURE_ALGORITHM)
        if not self.config.has_option(SECURITY_KEY, NTP_SERVER):
            self.add_ntp_server(DEFAULT_NTP_SERVER)

    def is_correct(self):
        return self.config.has_option(API_KEY, URL) and self.config.has_option(SECURITY_KEY, PRIVATE_KEY_PATH)
