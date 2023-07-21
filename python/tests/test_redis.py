import unittest

from dotenv import load_dotenv

from app.get_set_redis import TestRedis

load_dotenv()


class TestWeather(unittest.TestCase):
    def test_redis(self):
        result = TestRedis().set_get_redis("bar")
        print(result)
        self.assertEqual("bar", result)
