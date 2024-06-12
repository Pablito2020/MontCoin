from pydantic import BaseModel, Field

from schemas.users import CreateUser, DeleteUser


class Signed(BaseModel):
    signature: str = Field(
        description="The signature of the other fields plus a \"date\" field (in unix timestamp format). "
    )


class CreateUserSigned(Signed, CreateUser):
    pass


class DeleteUserSigned(Signed, DeleteUser):
    pass
