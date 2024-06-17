from dataclasses import dataclass


@dataclass
class Operation:
    amount: int
    should_fail_if_not_enough_money: bool
    with_credit_card: bool

    def to_dict(self) -> dict:
        return {
            "amount": self.amount,
            "should_fail_if_not_enough_money": self.should_fail_if_not_enough_money,
            "with_credit_card": self.with_credit_card,
        }