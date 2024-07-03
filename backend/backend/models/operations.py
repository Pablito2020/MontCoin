import time
import uuid
from fastapi import HTTPException
from typing import List

from sqlalchemy import Column, String, ForeignKey, Integer
from sqlalchemy.orm import Session
from starlette import status

from backend.models.database import Base
from backend.models.users import get_user_db_by_id, from_db_to_schema
from backend.schemas.operations import Operation
from backend.services.timeservice import get_current_utc_time, HourRange


class Operations(Base):
    __tablename__ = 'operations'

    id = Column(
        String, primary_key=True, index=True)
    user_id = Column(String, ForeignKey('users.id'), index=True)
    amount = Column(Integer)
    date = Column(Integer, index=True, default=time.time())


class BulkOperations(Base):
    __tablename__ = 'bulk_operations'

    id = Column(String, index=True, primary_key=True)
    operation_id = Column(String, ForeignKey('operations.id'), index=True, primary_key=True)
    amount = Column(Integer)


def create_operation_db(
        user_id: str,
        amount: int,
) -> Operations:
    if unix_time_es := get_current_utc_time():
        operation = Operations(id=str(uuid.uuid4()), user_id=user_id, amount=amount, date=unix_time_es)
        return operation
    raise HTTPException(status_code=status.HTTP_504_GATEWAY_TIMEOUT, detail="Couldn't get time")


def from_operation_db_to_schema(
        operation: Operations
) -> Operation:
    return Operation(
        id=operation.id,
        user=operation.user_id,
        amount=operation.amount,
        date=operation.date)


def do_operation(
        user_id: str,
        amount: int,
        done_with_credit_card: bool,
        db: Session
):
    user = get_user_db_by_id(user_id, db)
    operation = create_operation_db(user_id=user_id, amount=amount)
    user.amount += amount
    user.operations_with_card += 1 if done_with_credit_card else 0
    db.add(operation)
    db.commit()
    db.refresh(operation)
    user = from_db_to_schema(user)
    return Operation(
        id=operation.id,
        user=user,
        amount=operation.amount,
        date=operation.date
    )


def get_operations(
        db: Session
) -> List[Operation]:
    operations = []
    for operation in db.query(Operations).all():
        user = get_user_db_by_id(operation.user_id, db)
        schema_user = from_db_to_schema(user)
        operations.append(Operation(
            id=operation.id,
            user=schema_user,
            amount=operation.amount,
            date=operation.date
        ))
    return sorted(operations, key=lambda x: x.date, reverse=True)


def get_operations_for_user(
        user_id: str,
        db: Session
) -> List[Operation]:
    user = get_user_db_by_id(user_id, db)
    user_schema = from_db_to_schema(user)
    operations = []
    for operation in db.query(Operations).filter_by(user_id=user.id).all():
        operations.append(Operation(
            id=operation.id,
            user=user_schema,
            amount=operation.amount,
            date=operation.date
        ))
    return sorted(operations, key=lambda x: x.date, reverse=True)


def get_operations_inside_range(
        range: HourRange,
        db: Session
) -> List[Operation]:
    operations: List[Operation] = []
    for operation in db.query(Operations).filter(
            Operations.date > range.begin,
            Operations.date.between(range.begin, range.end)
    ).all():
        user = get_user_db_by_id(operation.user_id, db)
        user_schema = from_db_to_schema(user)
        operations.append(Operation(
            id=operation.id,
            user=user_schema,
            amount=operation.amount,
            date=operation.date
        ))
    return operations
