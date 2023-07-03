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
