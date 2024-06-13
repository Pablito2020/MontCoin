from functools import cache
from typing import Iterable

import requests

from admin.config.api import ApiUrl
from admin.config.config import Configuration
from admin.user.security import add_signature
from admin.user.user import User, Id


@cache
def get_base_url() -> str:
    return Configuration.get(ApiUrl)


def __signed_post(url: str, payload: dict) -> requests.Response:
    to_send = add_signature(payload)
    return requests.post(
        url,
        json=to_send,
    )


def __signed_delete(url: str, payload: dict) -> requests.Response:
    to_send = add_signature(payload)
    return requests.delete(
        url,
        json=to_send,
    )


def create_user(user: User) -> requests.Response:
    return __signed_post(
        url=f"{get_base_url()}/user/{user.id.value}",
        payload=user.to_dict(),
    )


def get_users() -> Iterable[User]:
    response = requests.get(f"{get_base_url()}/users")
    for user in response.json()["users"]:
        yield User.from_value(id=user["id"], username=user["name"], amount=user["amount"])


def delete_user(id: Id) -> requests.Response:
    return __signed_delete(
        url=f"{get_base_url()}/user/{id.value}",
        payload={
            "id": id.value
        },
    )
