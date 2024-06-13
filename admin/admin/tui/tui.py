import functools
from typing import Iterable, Callable

from rich.table import Table
from rich import print

from admin.user.user import User
import typer
import requests


def print_users(users: Iterable[User]):
    table = Table(title="Users")
    table.add_column("Id", justify="right", style="cyan", no_wrap=True)
    table.add_column("Name", style="magenta")
    table.add_column("Amount", justify="right", style="green")
    for user in users:
        table.add_row(str(user.id), user.username, str(user.amount))
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
