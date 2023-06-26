import json
import os
import sys

from util.redis_connection import RedisConnection

class TestRedis:
    client = RedisConnection.get_client()

    @classmethod
    def set_get_redis(cls, value):
        cls.client.set("foo", value)
        value = cls.client.get("foo")
        print(f"Value: {value}")
        return value

if __name__ == '__main__':
    TestRedis().set_get_redis("Welcome to test redis no TLS")
