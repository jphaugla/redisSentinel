openssl pkcs12 -export \
  -in ./scripts/tests/tls/redis.crt \
  -inkey ./scripts/tests/tls/redis.key \
  -out src/main/resources/tls/client-keystore.p12 \
  -passout pass:jasonrocks \
  -name redis
