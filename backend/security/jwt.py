from functools import cache

import jwt

from configuration.config import Configuration
from configuration.security import UsersPublicKeyPath, OperationsPublicKeyPath

ALGORITHM = "RS256"


@cache
def get_public_key(path: UsersPublicKeyPath | OperationsPublicKeyPath) -> str:
    public_key_file = Configuration.get(path)  # type: ignore
    with open(public_key_file, "r") as public_key:
        return public_key.read()


type jwt_token = str


def decode_user_token(token: jwt_token) -> dict:
    return jwt.decode(token, get_public_key(UsersPublicKeyPath), algorithms=[ALGORITHM])


def decode_operation_token(token: jwt_token) -> dict:
    return jwt.decode(token, get_public_key(OperationsPublicKeyPath), algorithms=[ALGORITHM])
