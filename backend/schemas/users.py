from pydantic import BaseModel, Field


# Users schema


class Id(BaseModel):
    id: str = Field(
        pattern="^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        examples=["5aecbceb-105f-4a76-96b0-303d07f024b7"],
        description="ID of the user. It is an UUID v4.",
    )


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


class Users(BaseModel):
    users: list[User] = Field(
        description="List of users",
        example=[
            User(
                id="5aecbceb-105f-4a76-96b0-303d07f024b7",
                name="John Doe",
                amount=10,
            ),
        ],
    )


# Crud operations


class DeleteUser(Id):
    pass


class CreateUser(UserAccountInformation):
    pass
