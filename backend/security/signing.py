from typing import TypeVar, Type

from schemas.security import Signed
from security.jwt import decode_user_token, decode_operation_token

T = TypeVar('T')
R = TypeVar('R', bound=Signed)


def assert_signature_user(request: R, type: Type[T]) -> T:
    without_sign = type(**decode_user_token(request.signature))
    assert (type(**request.dict()) == without_sign), "The fields should be the same as the signed one"
    return without_sign


def assert_signature_operation(request: R, type: Type[T]) -> T:
    without_sign = type(**decode_operation_token(request.signature))
    assert (type(**request.dict()) == without_sign), "The fields should be the same as the signed one"
    return without_sign
