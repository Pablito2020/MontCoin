import csv
from pathlib import Path
from typing import Callable, List, TypeVar, Iterable

T = TypeVar("T")


def read_csv(
        file_path: str, map_fn: Callable[[List[str]], T] = lambda x: x
) -> Iterable[T]:
    if not file_path.endswith(".csv"):
        raise ValueError("The file must be a csv file")
    if not Path(file_path).exists():
        raise FileNotFoundError("The file does not exist")
    with open(file_path, "r") as file:
        for row in csv.reader(file):
            yield map_fn(row)
