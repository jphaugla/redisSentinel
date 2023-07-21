import os

import redis
from redis.sentinel import Sentinel


class RedisConnection(object):
    ssl_certfile = "/scripts/tests/tls/redis.crt"
    ssl_keyfile = "/scripts/tests/tls/redis.key"
    ssl_ca_certs = "/scripts/tests/tls/ca.crt"
    username = os.getenv("REDIS_USER", None)
    redis_host = os.getenv("REDIS_HOST", "localhost")
    redis_port = os.getenv("REDIS_PORT", 6379)
    password = os.getenv("REDIS_PASSWORD", None)
    sentinel_host = os.getenv("SENTINEL_HOST", "localhost")
    sentinel_port = os.getenv("SENTINEL_PORT", 26379)
    sentinel_master = os.getenv("SENTINEL_MASTER", "mymaster")

    @staticmethod
    def get_client():
        return redis.StrictRedis(
            username=RedisConnection.username,
            host=RedisConnection.redis_host,
            port=RedisConnection.redis_port,
            password=RedisConnection.password,
            encoding="utf-8", decode_responses=True)

    @staticmethod
    def get_client_tls():
        return redis.StrictRedis(
            username=RedisConnection.username,
            host=RedisConnection.redis_host,
            port=RedisConnection.redis_port,
            password=RedisConnection.password,
            ssl=True,
            ssl_cert_reqs="required",
            ssl_certfile=RedisConnection.ssl_certfile,
            ssl_keyfile=RedisConnection.ssl_keyfile,
            ssl_ca_certs=RedisConnection.ssl_ca_certs,
            encoding="utf-8", decode_responses=True)

    @staticmethod
    def get_sentinel_client():
        sentinel = Sentinel([(RedisConnection.sentinel_host, RedisConnection.sentinel_port)], socket_timeout=0.1)
        master = sentinel.master_for(
            RedisConnection.sentinel_master,
            socket_timeout=0.1,
            username=RedisConnection.username,
            password=RedisConnection.password)
        return master

    @staticmethod
    def get_sentinel_client_tls():
        sentinel = Sentinel(
            [(RedisConnection.sentinel_host, RedisConnection.sentinel_port)],
            socket_timeout=0.1,
            ssl=True,
            ssl_ca_certs="/scripts/sentinel_tests/tls/CA-cert.pem",
            ssl_certfile="/scripts/sentinel_tests/tls/san.crt",
            ssl_keyfile="/scripts/sentinel_tests/tls/private.key")

        master = sentinel.master_for(
            RedisConnection.sentinel_master,
            socket_timeout=0.1,
            username=RedisConnection.username,
            password=RedisConnection.password)
        return master
