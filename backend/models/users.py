from sqlalchemy import Column, String, Integer

from models.database import Base


class Users(Base):
    __tablename__ = 'users'

    id = Column(
        String, primary_key=True, index=True
    )
    name = Column(String, unique=True)
    amount = Column(Integer, default=0)
    operations_with_card = Column(Integer, default=0)
