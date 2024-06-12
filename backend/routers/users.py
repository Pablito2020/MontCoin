import uuid

from fastapi import APIRouter
from starlette import status

from schemas.users import Users, User, CreateUser, DeleteUser
from schemas.security import CreateUserSigned, DeleteUserSigned
from security.jwt import decode_user_token

router = APIRouter(
    tags=["Users"],
)


@router.get(
    path="/users",
    summary="Get all users",
    response_description="List of all users",
    status_code=status.HTTP_200_OK,
    response_model=Users,
    responses={
        status.HTTP_410_GONE: {
            "description": "List of all users",
            "model": Users,
        },
    },
)
def get_users() -> Users:
    return Users(
        users=[
            User(
                id=str(uuid.uuid4()),
                name="John Doe",
            ),
            User(
                id=str(uuid.uuid4()),
                name="Mary Doe",
            ),
            User(
                id=str(uuid.uuid4()),
                name="Ultra Doe",
            ),
            User(
                id=str(uuid.uuid4()),
                name="Duck Doe",
            ),
        ]
    )


@router.get(
    path="/user/{user_id}",
    summary="Get a user given his id",
    response_description="User information",
    status_code=status.HTTP_200_OK,
    response_model=User,
    responses={
        status.HTTP_404_NOT_FOUND: {
            "description": "The user with this id was not found",
        },
    },
)
def get_user_id(user_id: str) -> User:
    return User(
        id=user_id,
        name="Duck Doe",
    )


@router.post(
    path="/user/{user_id}",
    summary="Get a user given his id",
    response_description="User information",
    status_code=status.HTTP_200_OK,
    response_model=User,
    responses={
        status.HTTP_404_NOT_FOUND: {
            "description": "The user with this id was not found",
        },
    },
)
def create_user_from_id(user_id: str, create_user_signed: CreateUserSigned) -> User:
    create_user = CreateUser(**decode_user_token(create_user_signed.signature))
    assert (
        CreateUser(**create_user_signed.dict()) == create_user
    ), "The fields should be the same as the signed one"
    return User(
        id=user_id,
        name=create_user.name,
        amount=create_user.amount,
    )


@router.delete(
    path="/user/{user_id}",
    summary="Delete a user and all his operations given his id",
    response_description="User information",
    status_code=status.HTTP_200_OK,
    response_model=User,
    responses={
        status.HTTP_404_NOT_FOUND: {
            "description": "The user with this id was not found",
        },
    },
)
def delete_user_from_id(user_id: str, request: DeleteUserSigned) -> User:
    delete_user = DeleteUser(**decode_user_token(request.signature))
    assert (
        DeleteUser(**request.dict()) == delete_user
    ), "The fields should be the same as the signed one"
    return User(
        id=user_id,
        name="unknown name",
    )
