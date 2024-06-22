from fastapi import APIRouter
from starlette import status

from models.database import db_dependency
from models.users import get_all_users
from schemas.security import CreateUserSigned, DeleteUserSigned
from schemas.users import Users, User
from services.users import delete_user_signed, create_user_signed, get_user_by_id

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
def get_users(db: db_dependency) -> Users:
    return get_all_users(db)


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
def get_user_id(user_id: str, db: db_dependency) -> User:
    return get_user_by_id(user_id, db)


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
def create_user_from_id(user_id: str, create_user_req_signed: CreateUserSigned, db: db_dependency) -> User:
    return create_user_signed(user_id, create_user_req_signed, db)


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
def delete_user_from_id(user_id: str, request: DeleteUserSigned, db: db_dependency) -> User:
    return delete_user_signed(user_id, request, db)
