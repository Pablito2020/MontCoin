from configparser import ConfigParser
from typing import Optional

from admin.config.config import ConfigurationOption

API_KEY = "Api"

URL = "url"


class ApiUrl(ConfigurationOption):
    @staticmethod
    def get_value(configuration: ConfigParser) -> Optional[str]:
        return configuration.get(API_KEY, URL)
