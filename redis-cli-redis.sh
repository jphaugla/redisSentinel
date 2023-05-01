redis-cli -p ${REDIS_PORT} -h ${REDIS_HOST} -a ${REDIS_PASSWORD} --user ${REDIS_USERNAME} --cert scripts/tests/tls/redis.crt --key scripts/tests/tls/redis.key --cacert scripts/tests/tls/ca.crt --tls
