from pydantic import Field, BaseModel

from backend.schemas.common import Id
from backend.schemas.users import User


class AmountOperation(BaseModel):
    amount: int = Field(
        description="The amount of MontCoin of the operation",
        examples=[10, -10]
    )


class Operation(Id, AmountOperation):
    user: User = Field(
        description="The user that made the operation",
    )
    date: int = Field(
        description="The date of the operation in unix timestamp format",
        examples=[1615760000]
    )


class OperationStats(BaseModel):
    positive_amount: int = Field(
        description="The amount of positive operations",
        examples=[10]
    )
    negative_amount: int = Field(
        description="The amount of negative operations",
        examples=[10]
    )
    hour: int = Field(
        description="The hour of the day of the operation",
        ge=0,
        le=24,
    )


class CreateBulkOperation(BaseModel):
    users: list[str] = Field(
        description="List of users ids",
        example=["5aecbceb-105f-4a76-96b0-303d07f024b7"],
    )
    amount: int = Field(
        description="The amount of MontCoin of the operation",
        examples=[10, -10]
    )


class CreatedBulkOperation(BaseModel):
    num_users: int = Field(
        description="Number of users that the operation was made",
        example=[100, 10],
    )
    amount: int = Field(
        description="The amount of MontCoin of the operation",
        examples=[10, -10]
    )


class WriteOperation(AmountOperation):
    should_fail_if_not_enough_money: bool = Field(
        default=False,
        description="If true, the operation will fail if the user does not have enough money",
    )
    with_credit_card: bool = Field(
        default=False,
        description="Indicates if the operation was made with a credit card. Needed for statistics",
    )
