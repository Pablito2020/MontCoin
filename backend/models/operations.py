import uuid

from sqlalchemy import Column, String, ForeignKey, Integer, Date

from models.database import Base


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
