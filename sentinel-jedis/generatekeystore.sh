rm -f src/main/resources/tls/*
openssl pkcs12 -export \
  -in ../scripts/sentinel_tests/tls/san.crt \
  -inkey ../scripts/sentinel_tests/tls/private.key \
  -out src/main/resources/tls/client-keystore.p12 \
  -passout pass:${KEYSTORE_PASSWORD} \
  -name redis
