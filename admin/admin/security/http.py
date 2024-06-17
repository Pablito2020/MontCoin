from typing import Type

import requests

from admin.config.security import UsersPrivateKeyPath, OperationsPrivateKeyPath
from admin.security.security import add_signature


def signed_post(url: str, payload: dict, key_type: Type[UsersPrivateKeyPath | OperationsPrivateKeyPath]) -> requests.Response:
    to_send = add_signature(payload, key_type)
    return requests.post(
        url,
        json=to_send,
    )


def signed_delete(url: str, payload: dict, key_type: Type[UsersPrivateKeyPath | OperationsPrivateKeyPath]) -> requests.Response:
    to_send = add_signature(payload, key_type)
    return requests.delete(
        url,
        json=to_send,
    )
