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
        if len(value) != 2:
            raise ValueError(f"Non valid values, cannot create user from: {value}")
        username, amount = value
        amount = amount or 0
        return User(id=Id.generate(), username=username, amount=amount)

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
