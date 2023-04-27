openssl pkcs12 -export \
  -in ./tests/tls/client.crt \
  -inkey ./tests/tls/client.key \
  -out src/main/resources/tls/client-keystore.p12 \
  -name "APP_01_P12"
