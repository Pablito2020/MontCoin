from typing import List

from fastapi import APIRouter
from fastapi_pagination import paginate, Page
from starlette import status

from backend.models.database import db_dependency
from backend.models.operations import get_operations, get_operations_for_user, do_bulk_operation
from backend.schemas.operations import Operation, OperationStats, CreateBulkOperation, CreatedBulkOperation
from backend.schemas.security import WriteOperationSigned, DeleteUserSigned, CreateBulkOperationSigned
from backend.security.signing import assert_signature_operation
from backend.services.operations import create_operation_for, get_operations_daily_stats
from backend.services.users import get_user_by_id

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


@router.get(
    path="/operations",
    summary="Get all operations",
    response_description="The operations in a list",
    status_code=status.HTTP_200_OK,
    response_model=Page[Operation]
)
def get_operations_route(db: db_dependency):
    return paginate(get_operations(db))


@router.get(
    path="/operations/today",
    summary="Get all operations done within 24 hours",
    response_description="The operations in a list",
    status_code=status.HTTP_200_OK,
    response_model=List[OperationStats]
)
def get_operations_stats_today_route(db: db_dependency):
    return get_operations_daily_stats(db)


@router.get(
    path="/operations/user/{user_id}",
    summary="Get all operations for a given user",
    response_description="The operations in a list",
    status_code=status.HTTP_200_OK,
    response_model=List[Operation]
)
def get_operations_for_user_id_route(user_id: str, db: db_dependency):
    user = get_user_by_id(user_id, db)
    return get_operations_for_user(user.id, db)


@router.post(
    path="/operations/bulk",
    summary="Create an operation for N users",
    status_code=status.HTTP_200_OK,
    response_model=CreatedBulkOperation
)
def create_bulk_operation(signed_request: CreateBulkOperationSigned, db: db_dependency):
    request = assert_signature_operation(request=signed_request, type=CreateBulkOperation)
    return do_bulk_operation(users_id=request.users, amount=request.amount, db=db)
