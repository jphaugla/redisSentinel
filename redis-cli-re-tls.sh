redis-cli -p ${REDIS_PORT} -h ${REDIS_HOST} -a ${REDIS_PASSWORD} --user ${REDIS_USERNAME} --cert re_keys/tls/client_cert_app_001.pem --key re_keys/tls/client_key_app_001.pem --cacert re_keys/tls/proxy_cert.pem --tls
