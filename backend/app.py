from contextlib import contextmanager

from fastapi import FastAPI
from starlette.middleware.cors import CORSMiddleware
from starlette.responses import RedirectResponse

import models
from models.database import engine

from routers import users

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
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
    expose_headers=["*"],
)

models.database.Base.metadata.create_all(engine)



@app.get("/")
def root():
    return RedirectResponse(url="/docs")
