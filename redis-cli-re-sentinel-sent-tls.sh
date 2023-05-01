#  this is using the sentinel host, it is validating that the database is connecting correctly with the keys to sentinel host
redis-cli -p ${SENTINEL_PORT} -h ${SENTINEL_HOST} --cert sentinel_keys/tls/cert.pem --key sentinel_keys/tls/key.pem --cacert sentinel_keys/tls/proxy_cert.pem --tls
