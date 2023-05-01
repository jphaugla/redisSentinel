rm -f src/main/resources/tls/*
openssl pkcs12 -export \
  -in ../re_keys/tls/client_cert_app_001.pem \
  -inkey ../re_keys/tls/client_key_app_001.pem \
  -out src/main/resources/tls/client-keystore.p12 \
  -passout pass:${KEYSTORE_PASSWORD} \
  -name redis
