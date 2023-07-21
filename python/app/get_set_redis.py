from util.redis_connection import RedisConnection


class TestRedis:
    client = RedisConnection.get_client()

    @classmethod
    def set_get_redis(cls, value):
        print(f"Value: {value}")
        cls.client.set("test-no-tls", value)
        redis_value = cls.client.get("test-no-tls")
        print(f"Redis Value: {redis_value}")
        return redis_value


if __name__ == '__main__':
    TestRedis().set_get_redis("Welcome to test redis no TLS")
