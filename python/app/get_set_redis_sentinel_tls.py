from util.redis_connection import RedisConnection


class TestRedisSentinelTLS:
    client = RedisConnection.get_sentinel_client_tls()

    @classmethod
    def set_get_redis(cls, value):
        print(f"Value: {value}")
        cls.client.set("test-sentinel-with-tls", value)
        redis_value = cls.client.get("test-sentinel-with-tls")
        print(f"Redis Value: {redis_value}")
        return redis_value


if __name__ == '__main__':
    TestRedisSentinelTLS().set_get_redis("Welcome to TestRedisSentinel with TLS")
