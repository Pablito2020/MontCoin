from fastapi import FastAPI
from fastapi_pagination import add_pagination
from starlette.middleware.cors import CORSMiddleware
from starlette.responses import RedirectResponse

import backend.models
from backend.models.database import engine
from backend.routers import users, operations

app = FastAPI(
    title="MontCoin API",
    description="Create and read transactions for the MontCoin currency",
    docs_url="/docs",
    redoc_url="/redoc",
    version="0.1.0",
    swagger_ui_parameters={"syntaxHighlight.theme": "obsidian"},
)
app.router.redirect_slashes = False
app.include_router(users.router)
app.include_router(operations.router)
add_pagination(app)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
    expose_headers=["*"],
)

backend.models.database.Base.metadata.create_all(engine)


@app.get("/")
def root():
    return RedirectResponse(url="/docs")
