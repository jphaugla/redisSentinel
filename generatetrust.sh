keytool -genkey \
  -dname "cn=CLIENT_APP_01" \
  -alias truststorekey \
  -keyalg RSA \
  -keystore ./src/main/resources/tls/client-truststore.p12 \
  -storepass ${TRUSTSTORE_PASSWORD} \
  -storetype pkcs12
