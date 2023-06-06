keytool -import \
  -file ../scripts/sentinel_tests/tls/CA-cert.pem \
  -alias redis-ca \
  -keystore ./src/main/resources/tls/client-truststore.jks \
  -storepass ${TRUSTSTORE_PASSWORD}
