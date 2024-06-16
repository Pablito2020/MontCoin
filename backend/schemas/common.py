from pydantic import BaseModel, Field

IdType = Field(
    pattern="^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
    examples=["5aecbceb-105f-4a76-96b0-303d07f024b7"],
    description="ID in UUID v4 format",
)


class Id(BaseModel):
    id: str = Field(
        pattern="^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        examples=["5aecbceb-105f-4a76-96b0-303d07f024b7"],
        description="ID in UUID v4 format",
    )
