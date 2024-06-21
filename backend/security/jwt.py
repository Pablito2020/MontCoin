import datetime
import logging
from functools import cache
from fastapi import HTTPException
from typing import Type

import jwt
from starlette import status

from configuration.config import Configuration
from configuration.security import UsersPublicKeyPath, OperationsPublicKeyPath
from services.timeservice import get_current_spain_datetime

ALGORITHM = "RS256"
ALLOWED_DELAY_ON_DATE = datetime.timedelta(seconds=10)

type jwt_token = str


@cache
def get_public_key(path: Type[UsersPublicKeyPath | OperationsPublicKeyPath]) -> str:
    public_key_file = Configuration.get(path)
    with open(public_key_file, "r") as public_key:
        return public_key.read()


def __assert_correct_timestamp_and_decode(token: jwt_token, path: Type[UsersPublicKeyPath | OperationsPublicKeyPath],
                                          timestamp: datetime) -> dict:
    try:
        payload = jwt.decode(token, get_public_key(path), algorithms=[ALGORITHM])
        date = payload.get("date")
        if not date or datetime.datetime.fromtimestamp(date) > timestamp + ALLOWED_DELAY_ON_DATE:
            logging.warning(f"Date on signature is null or too far in the future: {token}")
        return payload
    except Exception:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Invalid Signature.")


def __decode(token: jwt_token, path: Type[UsersPublicKeyPath | OperationsPublicKeyPath]) -> dict:
    if unix_time := get_current_spain_datetime():
        return __assert_correct_timestamp_and_decode(token, path, timestamp=unix_time)
    raise ValueError("Couldn't fetch time.")


def decode_user_token(token: jwt_token) -> dict:
    return __decode(token, UsersPublicKeyPath)


def decode_operation_token(token: jwt_token) -> dict:
    return __decode(token, OperationsPublicKeyPath)
