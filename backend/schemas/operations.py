from pydantic import Field, BaseModel

from schemas.common import Id
from schemas.users import User


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


class WriteOperation(AmountOperation):
    should_fail_if_not_enough_money: bool = Field(
        default=False,
        description="If true, the operation will fail if the user does not have enough money",
    )
    with_credit_card: bool = Field(
        default=False,
        description="Indicates if the operation was made with a credit card. Needed for statistics",
    )
