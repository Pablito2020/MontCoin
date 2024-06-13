import uuid
from dataclasses import dataclass
from typing import List


@dataclass(frozen=True)
class Id:
    value: str

    def __post_init__(self):
        uuid.UUID(self.value)

    @staticmethod
    def generate() -> "Id":
        return Id(value=str(uuid.uuid4()))

    def __str__(self):
        return self.value


@dataclass(frozen=True)
class User:
    id: Id
    username: str
    amount: int

    @staticmethod
    def from_strings(value: List[str]) -> "User":
        if not value:
            raise ValueError(f"Non specified values, cannot create user from: {value}")
        if len(value) == 1:
            return User(id=Id.generate(), username=value[0], amount=0)
        if len(value) == 2:
            return User(id=Id.generate(), username=value[0], amount=int(value[1]))
        raise ValueError(f"Too many values, cannot create user from: {value}")

    @staticmethod
    def from_value(id: str | None, username: str, amount: int | None) -> "User":
        id = id or Id.generate().value
        amount = amount or 0
        return User(id=Id(id), username=username, amount=amount)

    def to_dict(self) -> dict:
        return {
            "id": self.id.value,
            "name": self.username,
            "amount": self.amount,
        }
