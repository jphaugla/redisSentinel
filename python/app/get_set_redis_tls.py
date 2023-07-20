from util.redis_connection import RedisConnection


class TestRedisTLS:
    client = RedisConnection.get_client_tls()

    @classmethod
    def set_get_redis(cls, value):
        print(f"Value: {value}")
        silly_val = cls.client.get("silly")
        print(f"Silly value: {silly_val}")
        cls.client.set("test-with-tls", value)
        redis_value = cls.client.get("test-with-tls")
        print(f"Value: {redis_value}")
        return redis_value


if __name__ == '__main__':
    TestRedisTLS().set_get_redis("Welcome to test redis with TLS")
