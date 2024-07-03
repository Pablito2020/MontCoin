# TODO: This is very dirty.
import datetime
from dataclasses import dataclass
from typing import List

import ntplib


def get_current_utc_time() -> int | None:
    try:
        ntp_client = ntplib.NTPClient()
        return int(ntp_client.request('es.pool.ntp.org').tx_time)
    except ntplib.NTPException:
        return None


def get_current_utc_datetime() -> datetime.datetime | None:
    if unix_time_es := get_current_utc_time():
        return datetime.datetime.fromtimestamp(unix_time_es)


@dataclass
class HourRange:
    begin: int
    end: int
    from_current_time_differs_in_hours: int

    def __contains__(self, item: int):
        return self.begin < item <= self.end


def get_24_hour_range_from(utc: int) -> List[HourRange]:
    one_hour_in_utc = 3600
    hours = []
    for i in range(0, 24):
        begin = utc - one_hour_in_utc * (i + 1)
        end = utc - one_hour_in_utc * i
        hours.append(HourRange(begin=begin, end=end, from_current_time_differs_in_hours=i))
    return hours
