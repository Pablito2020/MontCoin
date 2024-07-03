from typing import List

from pydantic import BaseModel, Field

from backend.schemas.common import Id


# Users schema


class UserAccountInformation(BaseModel):
    name: str = Field(description="The name of the user", example="John Doe")
    amount: int = Field(
        default=0, description="The amount of MontCoin the user has", example=10
    )


class User(Id, UserAccountInformation):
    operations_with_card: int = Field(default=0, example=0,
                                      description="The number of operations the user has made with a card")


class UserStats(User):
    number_of_operations: int = Field(
        ge=0,
        default=0,
        description="The number of operations the user has made",
        example=10,
    )


Users = List[User]

# Crud operations


class DeleteUser(Id):
    pass


class CreateUser(UserAccountInformation):
    pass
