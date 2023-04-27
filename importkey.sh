keytool -import \
  -keystore ./src/main/resources/tls/client-truststore.p12 \
  -file ./tests/tls/server.crt \
  -alias redis-cluster-crt
