import os

import redis


class RedisConnection(object):

    @staticmethod
    def get_client():
        return redis.StrictRedis(
            host=os.getenv("REDIS_HOST", "localhost"),
            port=os.getenv("REDIS_PORT", 6379),
            password=os.getenv("REDIS_PASSWORD", ""),
            encoding="utf-8", decode_responses=True)

    @staticmethod
    def get_client_tls():
        ssl_certfile="/scripts/tests/tls/client.key"
        ssl_keyfile="/scripts/tests/tls/client.crt"
        ssl_ca_certs="/scripts/tests/tls/ca.crt"

        return redis.StrictRedis(
            host=os.getenv("REDIS_HOST", "localhost"),
            port=os.getenv("REDIS_PORT", 6379),
            password=os.getenv("REDIS_PASSWORD", ""),
            ssl=True,
            ssl_cert_reqs="required",
            ssl_certfile=ssl_certfile,
            ssl_keyfile=ssl_keyfile,
            ssl_ca_certs=ssl_ca_certs,
            encoding="utf-8", decode_responses=True)
