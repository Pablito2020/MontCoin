from fastapi import APIRouter
from starlette import status

from models.database import db_dependency
from schemas.users import Users, User, CreateUser, DeleteUser
from schemas.security import CreateUserSigned, DeleteUserSigned
from security.jwt import decode_user_token

from models.users import Users as UserDb

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
    return Users(
        users=[
            User(
                id=current_user.id,
                name=current_user.name,
                amount=current_user.amount,
                operations_with_card=current_user.operations_with_card,
            ) for current_user in db.query(UserDb).all()
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
def get_user_id(user_id: str, db: db_dependency) -> User:
    current_user = db.query(UserDb).filter_by(id=user_id).first()
    return User(
        id=current_user.id,
        name=current_user.name,
        amount=current_user.amount,
        operations_with_card=current_user.operations_with_card,
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
def create_user_from_id(user_id: str, create_user_signed: CreateUserSigned, db: db_dependency) -> User:
    create_user = CreateUser(**decode_user_token(create_user_signed.signature))
    assert (
            CreateUser(**create_user_signed.dict()) == create_user
    ), "The fields should be the same as the signed one"
    new_user = UserDb(
        id=user_id,
        name=create_user.name,
        amount=0,
    )
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    return User(
        id=new_user.id,
        name=new_user.name,
        amount=new_user.amount,
        operations_with_card=new_user.operations_with_card,
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
def delete_user_from_id(user_id: str, request: DeleteUserSigned, db: db_dependency) -> User:
    delete_user = DeleteUser(**decode_user_token(request.signature))
    assert (
            DeleteUser(**request.dict()) == delete_user
    ), "The fields should be the same as the signed one"
    user = db.query(UserDb).filter_by(id=user_id).first()
    if not user:
        return status.HTTP_404_NOT_FOUND
    db.delete(user)
    db.commit()
    return User(
        id=user.id,
        name=user.name,
        amount=user.amount,
        operations_with_card=user.operations_with_card,
    )
