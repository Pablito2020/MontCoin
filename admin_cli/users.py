import jwt

ALGORITHM = "RS256"

def add_user(
    name: str,
    amount: int | None = None,
    ) -> None:
    to_send = {"name": name, "amount": amount}
    signature = jwt.encode(to_send, algorithm=ALGORITHM)
    to_send["signature"] = signature
    print(to_send)


add_user(
        name="Pablo Fraile Alonso",
        amount = 100
)
