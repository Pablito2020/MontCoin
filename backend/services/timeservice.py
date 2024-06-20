# TODO: This is very dirty.
import datetime

import ntplib


def get_current_time_spain_on_utc() -> int | None:
    spain_delay = 7200
    try:
        ntp_client = ntplib.NTPClient()
        return int(ntp_client.request('es.pool.ntp.org').tx_time) + spain_delay
    except ntplib.NTPException:
        return None


def get_current_spain_datetime() -> datetime.datetime | None:
    if unix_time_es := get_current_time_spain_on_utc():
        return datetime.datetime.fromtimestamp(unix_time_es)
