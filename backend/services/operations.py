from typing import List

from fastapi import HTTPException
from sqlalchemy.orm import Session
from starlette import status

from models.operations import do_operation, get_operations_inside_range
from schemas.operations import WriteOperation, Operation, OperationStats, CreateBulkOperation
from schemas.security import WriteOperationSigned, CreateBulkOperationSigned
from schemas.users import User
from security.signing import assert_signature_operation
from services.timeservice import get_24_hour_range_from, get_current_utc_time, HourRange
from services.users import get_user_by_id


def create_operation_for(user_id: str, write_operation: WriteOperationSigned, db: Session) -> Operation:
    safe_write_operation = assert_signature_operation(request=write_operation, type=WriteOperation)
    return do_operation_for(user_id=user_id, write_operation=safe_write_operation, db=db)


def do_operation_for(user_id: str, write_operation: WriteOperation, db: Session) -> Operation:
    user = get_user_by_id(user_id, db)
    return do_operation_for_user(user=user, write_operation=write_operation, db=db)


def do_operation_for_user(user: User, write_operation: WriteOperation, db: Session) -> Operation:
    if user.amount + write_operation.amount < 0 and write_operation.should_fail_if_not_enough_money:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,
                            detail=f"User {user.id} does not have enough money. And you requested to fail if not enough money.")
    return do_operation(
        user_id=user.id,
        amount=write_operation.amount,
        done_with_credit_card=write_operation.with_credit_card,
        db=db
    )


def get_stats_from(operations: List[Operation], hour_range: HourRange) -> OperationStats:
    positive_total_amount = sum([operation.amount for operation in operations if operation.amount > 0])
    negative_total_amount = sum([operation.amount for operation in operations if operation.amount < 0]) * -1
    return OperationStats(
        positive_amount=positive_total_amount,
        negative_amount=negative_total_amount,
        hour=hour_range.from_current_time_differs_in_hours
    )


def get_operations_daily_stats(db: Session) -> List[OperationStats]:
    time = get_current_utc_time()
    hour_ranges = get_24_hour_range_from(time)
    stats = []
    for hour_range in hour_ranges:
        operations = get_operations_inside_range(hour_range, db)
        stats.append(get_stats_from(operations, hour_range))
    return stats


def create_bulk_operation(bulk_operation_signed: CreateBulkOperationSigned, db: Session) -> List[Operation]:
    safe_bulk_operation = assert_signature_operation(request=bulk_operation_signed, type=CreateBulkOperation)
    users = [get_user_by_id(user_id, db) for user_id in safe_bulk_operation.users]
    return [do_operation_for_user(user, write_operation=WriteOperation(amount=safe_bulk_operation.amount), db=db) for user in users]