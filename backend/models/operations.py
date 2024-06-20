import time
import uuid
import datetime
from typing import List

import ntplib
from sqlalchemy import Column, String, ForeignKey, Integer
from sqlalchemy.orm import Session

from models.database import Base
from models.users import get_user_db_by_id, from_db_to_schema
from schemas.operations import Operation
from services.timeservice import get_current_time_spain_on_utc


class Operations(Base):
    __tablename__ = 'operations'

    id = Column(
        String, primary_key=True, index=True, default=str(uuid.uuid4())
    )
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
    if unix_time_es := get_current_time_spain_on_utc():
        operation = Operations(user_id=user_id, amount=amount, date=unix_time_es)
        return operation
    raise ValueError("Couldn't get time")


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


def list_operations(
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
    return operations
