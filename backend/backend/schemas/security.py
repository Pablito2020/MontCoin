from pydantic import BaseModel, Field

from backend.schemas.operations import WriteOperation, CreateBulkOperation
from backend.schemas.users import CreateUser, DeleteUser


class Signed(BaseModel):
    signature: str = Field(
        description="The signature of the other fields plus a \"date\" field (in unix timestamp format). "
    )


class CreateUserSigned(Signed, CreateUser):
    pass


class DeleteUserSigned(Signed, DeleteUser):
    pass


class CreateBulkOperationSigned(Signed, CreateBulkOperation):
    pass


class WriteOperationSigned(Signed, WriteOperation):
    pass
