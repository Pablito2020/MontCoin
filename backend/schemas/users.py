from pydantic import BaseModel, Field


class CreateUser(BaseModel):
    name: str = Field(description="The name of the user", example="John Doe")
    amount: int = Field(default=0, description="The amount of MontCoin the user has")


class CreateUserSigned(CreateUser):
    signature: str = Field(description="The signature of the name and amount json. It is a JWT token that is "
                                       "generated from the private key of the user", )


class User(CreateUser):
    id: str = Field(
        pattern='^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$',
        examples=['123e4567-e89b-12d3-a456-426614174000'],
        description='ID of the user. It is an UUID v4.',
    )


class Users(BaseModel):
    users: list[User] = Field(description="List of users")


class UserInformation(User):
    number_of_operations: int = Field(ge=0, default=0, description="The number of operations the user has made",
                                      example=10)
