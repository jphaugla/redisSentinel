import os

import redis
from redis.sentinel import Sentinel


class RedisConnection(object):

    @staticmethod
    def get_client():
        return redis.StrictRedis(
            username=os.getenv("REDIS_USER", None),
            host=os.getenv("REDIS_HOST", "localhost"),
            port=os.getenv("REDIS_PORT", 6379),
            password=os.getenv("REDIS_PASSWORD", None),
            encoding="utf-8", decode_responses=True)

    @staticmethod
    def get_client_tls():
        ssl_certfile = "/scripts/tests/tls/redis.crt"
        ssl_keyfile = "/scripts/tests/tls/redis.key"
        ssl_ca_certs = "/scripts/tests/tls/ca.crt"

        return redis.StrictRedis(
            username=os.getenv("REDIS_USER", None),
            host=os.getenv("REDIS_HOST", "localhost"),
            port=os.getenv("REDIS_PORT", 6379),
            password=os.getenv("REDIS_PASSWORD", None),
            ssl=True,
            ssl_cert_reqs="required",
            ssl_certfile=ssl_certfile,
            ssl_keyfile=ssl_keyfile,
            ssl_ca_certs=ssl_ca_certs,
            encoding="utf-8", decode_responses=True)

    @staticmethod
    def get_sentinel_client():
        username = os.getenv("REDIS_USER", None)
        password = os.getenv("REDIS_PASSWORD", None)
        sentinel_host = os.getenv("SENTINEL_HOST", "localhost")
        sentinel_port = os.getenv("SENTINEL_PORT", 26379)
        sentinel_master = os.getenv("SENTINEL_MASTER", "mymaster")

        sentinel = Sentinel([(sentinel_host, sentinel_port)], socket_timeout=0.1)
        master = sentinel.master_for(sentinel_master, socket_timeout=0.1, username=username, password=password)
        return master
