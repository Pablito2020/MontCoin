from datetime import datetime
from functools import cache

import requests

from admin.config.api import ApiUrl
from admin.config.config import Configuration
from admin.config.security import OperationsPrivateKeyPath
from admin.operation.operation import CreateOperation, Operation
from admin.security.http import signed_post
from admin.user.user import User

key_type = OperationsPrivateKeyPath


@cache
def get_base_url() -> str:
    return Configuration.get(ApiUrl)


def create_operation_for(user_id: str, operation: CreateOperation):
    return signed_post(
        url=f"{get_base_url()}/operation/user/{user_id}",
        payload=operation.to_dict(),
        key_type=OperationsPrivateKeyPath
    )


def get_operations():
    result = requests.get(
        url=f"{get_base_url()}/operations",
    )
    for operation in result.json():
        yield Operation.from_value(id=operation["id"],
                                   date=datetime.utcfromtimestamp(operation['date']).strftime('%Y-%m-%d %H:%M:%S'),
                                   amount=operation["amount"],
                                   user=User.from_value(id=operation["user"]["id"], amount=operation["user"]["amount"],
                                                        username=operation["user"]["name"])
                                   )
