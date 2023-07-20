from util.redis_connection import RedisConnection


class TestRedisSentinel:
    client = RedisConnection.get_sentinel_client()

    @classmethod
    def set_get_redis(cls, value):
        print(f"Value: {value}")
        cls.client.set("test-sentinel-no-tls", value)
        redis_value = cls.client.get("test-sentinel-no-tls")
        print(f"Redis Value: {redis_value}")
        return redis_value


if __name__ == '__main__':
    TestRedisSentinel().set_get_redis("Welcome to TestRedisSentinel no TLS")
