from functools import cache

from admin.config.api import ApiUrl
from admin.config.config import Configuration
from admin.config.security import OperationsPrivateKeyPath
from admin.operation.operation import Operation
from admin.security.http import signed_post

key_type = OperationsPrivateKeyPath


@cache
def get_base_url() -> str:
    return Configuration.get(ApiUrl)


def create_operation_for(user_id: str, operation: Operation):
    return signed_post(
        url=f"{get_base_url()}/operation/user/{user_id}",
        payload=operation.to_dict(),
        key_type=OperationsPrivateKeyPath
    )
