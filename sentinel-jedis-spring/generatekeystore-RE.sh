rm -f src/main/resources/tls/*
openssl pkcs12 -export \
  -in ../sentinel_keys/tls/cert.pem \
  -inkey ../sentinel_keys/tls/key.pem \
  -out src/main/resources/tls/client-keystore.p12 \
  -passout pass:${KEYSTORE_PASSWORD} \
  -name redis
