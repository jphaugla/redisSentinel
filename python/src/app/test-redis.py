import json
import os
import sys

from util.redis_connection import RedisConnection


redis = RedisConnection.get_client()

redis.set("foo", "bar")
value = redis.get("foo")
print(f"Welcome to Python no TLS, value: {value}")
