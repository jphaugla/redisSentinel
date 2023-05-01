keytool -import \
  -file ../scripts/tests/tls/redis.crt \
  -alias redis-ca \
  -keystore ./src/main/resources/tls/client-truststore.jks \
  -storepass ${TRUSTSTORE_PASSWORD}
