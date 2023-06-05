redis-cli -p ${SENTINEL_PORT} --cert scripts/sentinel_tests/tls/san.crt --key scripts/sentinel_tests/tls/private.key --cacert scripts/sentinel_tests/tls/CA-cert.pem --tls
