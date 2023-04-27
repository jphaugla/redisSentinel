# redis-cli -p 6379 -a jasonrocks --user jph --cert tests/tls/redis.crt --key tests/tls/redis.key --cacert tests/tls/ca.crt --tls
redis-cli -p 6379 -a jasonrocks --cert scripts/tests/tls/redis.crt --key scripts/tests/tls/redis.key --cacert scripts/tests/tls/ca.crt --tls
