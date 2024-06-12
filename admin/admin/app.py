import functools
import uuid
from typing import Callable

import requests
import typer
from rich import print
from rich.progress import track
from rich.table import Table

from admin.security import add_signature

app = typer.Typer()

URL = "http://localhost:8000"


def pretty_print(func: Callable[..., requests.Response]):
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        result = func(*args, **kwargs)
        if result.status_code == 200:
            typer.echo(f"Operation: {func.__name__} done correctly! 🚀 ")
        else:
            typer.echo(f"Error in operation: {func.__name__}")
            print(result)

    return wrapper


def send_post(uri: str, payload: dict) -> requests.Response:
    to_send = add_signature(payload)
    return requests.post(
        f"{URL}{uri}",
        json=to_send,
    )


def __create_user(username: str, id: str = str(uuid.uuid4()), amount: int = 0) -> requests.Response:
    return send_post(uri=f"/user/{id}", payload={"name": username, "amount": amount})


@app.command(
    name="create",
    help="Creates a user in the system",
)
@pretty_print
def create_user(username: str, id: str = str(uuid.uuid4()), amount: int = 0):
    return __create_user(id=id, username=username, amount=amount)


@app.command(
    name="create-all",
    help="Creates all the users from the csv file",
)
def create_all_users():
    for user in track(range(100), description="Creating users"):
        __create_user(username=f"User {user}")


@app.command(
    name="list",
    help="List all the users in the system",
)
@pretty_print
def list_all_users():
    r = requests.get(f"{URL}/users")
    table = Table(title="Users")
    table.add_column("Id", justify="right", style="cyan", no_wrap=True)
    table.add_column("Name", style="magenta")
    table.add_column("Amount", justify="right", style="green")
    for user in r.json()["users"]:
        table.add_row(user["id"], user["name"], str(user["amount"]))
    print(table)
    return r


@app.command(
    name="delete",
    help="Delete a user from the system",
)
def list_all_users():
    print("Hello World!")


@app.command(
    name="delete all",
    help="Delete all the users from the system",
)
def list_all_users():
    print("Hello World!")


def main():
    app()
