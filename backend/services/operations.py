from fastapi import HTTPException
from sqlalchemy.orm import Session
from starlette import status

from models.operations import do_operation
from schemas.operations import WriteOperation, Operation
from schemas.security import WriteOperationSigned
from security.signing import assert_signature_operation
from services.users import get_user_by_id


def create_operation_for(user_id: str, write_operation: WriteOperationSigned, db: Session) -> Operation:
    safe_write_operation = assert_signature_operation(request=write_operation, type=WriteOperation)
    user = get_user_by_id(user_id, db)
    if user.amount + safe_write_operation.amount < 0 and safe_write_operation.should_fail_if_not_enough_money:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,
                            detail=f"User {user_id} does not have enough money. And you requested to fail if not enough money.")
    return do_operation(
        user_id=user_id,
        amount=safe_write_operation.amount,
        done_with_credit_card=safe_write_operation.done_with_credit_card,
        db=db
    )
