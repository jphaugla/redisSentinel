#!/bin/bash
java -jar /usr/app/redisentinel-0.0.1-SNAPSHOT.jar -Djavax.net.ssl.key-store=/usr/app/tls/client-keystore.p12 -Djavax.net.ssl.key-store-password=${KEYSTORE_PASSWORD} -Djavax.net.ssl.trust-store=/usr/app/tls/client-truststore.jks -Djavax.net.ssl.trust-store-password=${TRUSTSTORE_PASSWORD}
