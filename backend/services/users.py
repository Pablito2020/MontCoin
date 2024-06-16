from fastapi import HTTPException
from sqlalchemy.orm import Session
from starlette import status

from models.users import get_user_from_db_by_id, get_user_by_name, delete_user as delete_user_, \
    create_user as create_user_
from schemas.security import DeleteUserSigned, CreateUserSigned
from schemas.users import DeleteUser, User, CreateUser
from security.signing import assert_signature_user


def get_user_by_id(id: str, db: Session):
    user = get_user_from_db_by_id(id, db=db)
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"No user found with id: {id}")
    return user


def create_user_signed(id: str, create_user_req_signed: CreateUserSigned, db: Session):
    new_user = assert_signature_user(request=create_user_req_signed, type=CreateUser)
    user = User(id=id, name=new_user.name, amount=new_user.amount)
    return create_user(user=user, db=db)


def create_user(user: User, db: Session):
    user_with_same_name = get_user_by_name(user.name, db)
    if user_with_same_name:
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="User with this name already exists")
    create_user_(user=user, db=db)
    return user


def delete_user_signed(user_id: str, request: DeleteUserSigned, db: Session):
    del_user = assert_signature_user(request=request, type=DeleteUser)
    if del_user.id != user_id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Bad signature request")
    return delete_user(user_id=user_id, db=db)


def delete_user(user_id: str, db: Session) -> User:
    user = get_user_from_db_by_id(user_id, db)
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"No user found with id: {user_id}")
    delete_user_(user=user, db=db)
    return user
