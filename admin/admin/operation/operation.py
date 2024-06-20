from dataclasses import dataclass

from admin.user.user import User


@dataclass
class CreateOperation:
    amount: int
    should_fail_if_not_enough_money: bool
    with_credit_card: bool

    def to_dict(self) -> dict:
        return {
            "amount": self.amount,
            "should_fail_if_not_enough_money": self.should_fail_if_not_enough_money,
            "with_credit_card": self.with_credit_card,
        }


@dataclass(frozen=True)
class Operation:
    id: str
    amount: int
    user: User
    date: str

    @staticmethod
    def from_value(
            id: str,
            amount: int,
            date: str,
            user: User,
    ) -> "Operation":
        return Operation(
            id=id,
            user=user,
            amount=amount,
            date=date
        )
