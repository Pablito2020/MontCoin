import functools
from typing import Iterable, Callable

import requests
import typer
from rich import print
from rich.table import Table

from admin.operation.operation import Operation
from admin.user.user import User


def print_users(users: Iterable[User]):
    table = Table(title="Users")
    table.add_column("Id", justify="right", style="cyan", no_wrap=True)
    table.add_column("Name", style="magenta")
    table.add_column("Amount", justify="right", style="green")
    for user in users:
        table.add_row(str(user.id), user.username, str(user.amount))
    print(table)


def print_operations(operations: Iterable[Operation]):
    table = Table(title="Operations")
    table.add_column("Id", justify="right", style="cyan", no_wrap=True)
    table.add_column("User Id", style="magenta")
    table.add_column("User Name", style="magenta")
    table.add_column("User Amount", style="magenta")
    table.add_column("Amount", justify="right", style="green")
    table.add_column("Date", justify="right", style="red")
    for operation in operations:
        table.add_row(str(operation.id), str(operation.user.id), operation.user.username, str(operation.user.amount),
                      str(operation.amount), operation.date)
    print(table)


def informative_command(func: Callable[..., requests.Response]):
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        result = func(*args, **kwargs)
        if result.status_code == 200:
            typer.echo(f"Operation: {func.__name__} done correctly! 🚀 ")
        else:
            typer.echo(f"Error in operation: {func.__name__}")
            print(result)

    return wrapper
