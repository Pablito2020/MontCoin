import jwt
import ntplib

NTP_SERVER = 'es.pool.ntp.org'
ALGORITHM = "RS256"
PRIVATE_KEY="../certificates/create_users/private_key.pem"


def get_current_time() -> int | None:
    try:
        ntp_client = ntplib.NTPClient()
        return ntp_client.request(NTP_SERVER).tx_time
    except ntplib.NTPException:
        return None


def add_signature_with_key(data: dict, key: str) -> dict:
    to_sign = data.copy()
    to_sign["date"] = get_current_time()
    signature = jwt.encode(payload=to_sign, key=key, algorithm=ALGORITHM)
    to_send = data.copy()
    to_send["signature"] = signature
    return to_send


def add_signature(data: dict) -> dict:
    with open(PRIVATE_KEY, "r") as private_key:
        key = private_key.read()
        return add_signature_with_key(data, key)
