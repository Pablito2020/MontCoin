from sqlalchemy import Column, String, Integer
from sqlalchemy.orm import Session

from models.database import Base
from schemas.users import User


class _Users(Base):
    __tablename__ = 'users'

    id = Column(
        String, primary_key=True, index=True
    )
    name = Column(String, unique=True, index=True)
    amount = Column(Integer, default=0)
    operations_with_card = Column(Integer, default=0)


def from_schema_to_db(user: User) -> _Users:
    return _Users(
        id=user.id,
        name=user.name,
        amount=user.amount,
        operations_with_card=user.operations_with_card,
    )


def from_db_to_schema(user: _Users) -> User:
    return User(
        id=user.id,
        name=user.name,
        amount=user.amount,
        operations_with_card=user.operations_with_card,
    )


def delete_user(user: User, db: Session):
    db_user = get_user_db_by_id(user.id, db)
    assert db_user is not None, "User not found in the database."
    db.delete(db_user)
    db.commit()


def get_user_db_by_id(user_id: str, db: Session) -> _Users | None:
    return db.query(_Users).filter_by(id=user_id).first()


def get_user_by_id(user_id: str, db: Session) -> User | None:
    db_user = get_user_db_by_id(user_id, db)
    return from_db_to_schema(db_user) if db_user else None


def get_user_by_name(name: str, db: Session) -> User | None:
    db_user = db.query(_Users).filter_by(name=name).first()
    return from_db_to_schema(db_user) if db_user else None


def get_all_users(db: Session) -> list[User]:
    return [from_db_to_schema(user) for user in db.query(_Users).all()]


def create_user(user: User, db: Session):
    new_user = from_schema_to_db(user)
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
