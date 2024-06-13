from typing import Union

import typer
from rich.progress import track

from admin.config.config_file_builder import ConfigFileBuilder, DEFAULT_SIGNATURE_ALGORITHM, DEFAULT_NTP_SERVER
from admin.config.csv_reader import read_csv
from admin.user.repository import create_user, get_users, delete_user
from admin.tui.tui import informative_command, print_users
from admin.user.user import User, Id

app = typer.Typer()


@app.command(
    name="create",
    help="Creates a user in the system",
)
@informative_command
def create_user_command(
    username: str, id: Union[str, None] = None, amount: Union[int, None] = None
):
    user = User.from_value(id=id, username=username, amount=amount)
    return create_user(user)


@app.command(
    name="create-all",
    help="Creates all the users from the csv file",
)
def create_all_users_command(csv_path: str):
    for user in track(
        read_csv(file_path=csv_path, map_fn=User.from_strings),
        description="Creating users..",
    ):
        create_user(user)


@app.command(
    name="list",
    help="List all the users in the system",
)
def list_all_users_command():
    users = get_users()
    print_users(users)


@app.command(
    name="delete",
    help="Delete a user from the system",
)
@informative_command
def delete_user_command(id: str):
    return delete_user(Id(id))


@app.command(
    name="delete-all",
    help="Delete all the users from the system",
)
def delete_all_users_command():
    users = get_users()
    for user in track(
        users,
        description="Deleting users..",
    ):
        delete_user(user.id)


@app.command(
    name="configure",
    help="Configure the settings of the application",
)
def config_command():
    builder = ConfigFileBuilder()
    (
        builder.add_api_url(typer.prompt("Backend API URL"))
        .add_user_private_key_path(typer.prompt("Your private key path"))
        .add_signature_algorithm(typer.prompt("Signature algorithm", default=DEFAULT_SIGNATURE_ALGORITHM))
        .add_ntp_server(typer.prompt("NTP server", default=DEFAULT_NTP_SERVER))
        .write_file("config.ini")
    )


def main():
    app()
