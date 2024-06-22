from functools import cache
from typing import Type

import jwt
import ntplib

from admin.config.config import Configuration
from admin.config.security import SignAlgorithm, UsersPrivateKeyPath, NtpServer, OperationsPrivateKeyPath


@cache
def get_private_key_file_path(type_key: Type[UsersPrivateKeyPath | OperationsPrivateKeyPath]) -> str | None:
    return Configuration.get(type_key)


@cache
def get_algorithm() -> str | None:
    return Configuration.get(SignAlgorithm)


@cache
def get_ntp_server() -> str | None:
    return Configuration.get(NtpServer)


def get_current_time() -> int | None:
    try:
        ntp_client = ntplib.NTPClient()
        return int(ntp_client.request(get_ntp_server()).tx_time)
    except ntplib.NTPException:
        return None


def add_signature_with_key(data: dict, key: str) -> dict:
    to_sign = data.copy()
    to_sign["date"] = get_current_time()
    signature = jwt.encode(payload=to_sign, key=key, algorithm=get_algorithm())
    to_send = data.copy()
    to_send["signature"] = signature
    return to_send


def add_signature(data: dict, type_key: Type[UsersPrivateKeyPath | OperationsPrivateKeyPath]) -> dict:
    with open(get_private_key_file_path(type_key=type_key), "r") as private_key:
        key = private_key.read()
        return add_signature_with_key(data, key)
