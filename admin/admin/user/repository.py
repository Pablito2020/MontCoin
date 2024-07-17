from functools import cache
from typing import Iterable

import requests

from admin.config.api import ApiUrl
from admin.config.config import Configuration
from admin.config.security import UsersPrivateKeyPath
from admin.security.http import signed_post, signed_delete
from admin.user.user import User, Id

key_type = UsersPrivateKeyPath


@cache
def get_base_url() -> str:
    return Configuration.get(ApiUrl)


def create_user(user: User) -> requests.Response:
    return signed_post(
        url=f"{get_base_url()}/user/{user.id.value}",
        payload=user.to_dict(),
        key_type=key_type,
    )


def get_users() -> Iterable[User]:
    response = requests.get(f"{get_base_url()}/users")
    for user in response.json():
        yield User.from_value(id=user["id"], username=user["name"], amount=user["amount"])


def delete_user(id: Id) -> requests.Response:
    return signed_delete(
        url=f"{get_base_url()}/user/{id.value}",
        payload={
            "id": id.value
        },
        key_type=key_type,
    )
