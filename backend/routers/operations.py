from fastapi import APIRouter
from starlette import status

from models.database import db_dependency
from schemas.operations import Operation
from schemas.security import WriteOperationSigned
from services.operations import create_operation_for

router = APIRouter(
    tags=["Operations"],
)


@router.post(
    path="/operation/user/{user_id}",
    summary="Do an operation for a given user",
    response_description="The operation details",
    status_code=status.HTTP_200_OK,
    response_model=Operation,
    responses={
        status.HTTP_400_BAD_REQUEST: {
            "description": "User does not have enough money and should_fail_if_not_enough_money is True",
        },
        status.HTTP_404_NOT_FOUND: {
            "description": "User not found",
        },
    },
)
def do_operation(user_id: str, write_operation: WriteOperationSigned, db: db_dependency) -> Operation:
    return create_operation_for(user_id=user_id, write_operation=write_operation, db=db)
