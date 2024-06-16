import uuid
from datetime import datetime

from sqlalchemy import Column, String, ForeignKey, Integer, Date
from sqlalchemy.orm import Session

from models.database import Base
from models.users import get_user_db_by_id, from_db_to_schema
from schemas.operations import Operation


class Operations(Base):
    __tablename__ = 'operations'

    id = Column(
        String, primary_key=True, index=True, default=str(uuid.uuid4())
    )
    user_id = Column(String, ForeignKey('users.id'), index=True)
    amount = Column(Integer)
    date = Column(Date, index=True)


class BulkOperations(Base):
    __tablename__ = 'bulk_operations'

    id = Column(String, index=True, primary_key=True)
    operation_id = Column(String, ForeignKey('operations.id'), index=True, primary_key=True)
    amount = Column(Integer)


def create_operation(
        user_id: str,
        amount: int,
) -> Operations:
    current_time = datetime.datetime.now()
    print(str(current_time))
    operation = Operations(user_id=user_id, amount=amount, date=current_time)
    return operation


def do_operation(
        user_id: str,
        amount: int,
        done_with_credit_card: bool,
        db: Session
):
    user = get_user_db_by_id(user_id, db)
    user.amount += amount
    user.operations_with_card += 1 if done_with_credit_card else 0
    operation = create_operation(user_id=user_id, amount=amount)
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
