#  this is not actually using the sentinel host, it is validating that the database is connecting correctly with the keys
redis-cli -p ${REDIS_PORT} -h ${REDIS_HOST} -a ${REDIS_PASSWORD} --user ${REDIS_USERNAME} --cert sentinel_keys/tls/cert.pem --key sentinel_keys/tls/key.pem --cacert sentinel_keys/tls/proxy_cert.pem --tls
