from typing import List

from fastapi import HTTPException
from sqlalchemy.orm import Session
from starlette import status

from models.operations import do_operation, get_operations_inside_range
from schemas.operations import WriteOperation, Operation, OperationStats
from schemas.security import WriteOperationSigned
from security.signing import assert_signature_operation
from services.timeservice import get_24_hour_range_from, get_current_time_spain_on_utc, HourRange
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
        done_with_credit_card=safe_write_operation.with_credit_card,
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
    time = get_current_time_spain_on_utc()
    hour_ranges = get_24_hour_range_from(time)
    stats = []
    for hour_range in hour_ranges:
        operations = get_operations_inside_range(hour_range, db)
        stats.append(get_stats_from(operations, hour_range))
    return stats
