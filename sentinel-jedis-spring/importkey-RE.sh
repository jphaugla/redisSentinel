keytool -import \
  -file ../sentinel_keys/tls/proxy_cert.pem \
  -alias redis-ca \
  -keystore ./src/main/resources/tls/client-truststore.jks \
  -storepass ${TRUSTSTORE_PASSWORD}
