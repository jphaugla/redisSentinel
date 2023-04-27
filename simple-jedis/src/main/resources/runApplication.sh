#!/bin/bash
java -jar /usr/app/jedisTLSTest-1.0-SNAPSHOT-jar-with-dependencies.jar -Djavax.net.debug=ssl -Djavax.net.ssl.key-store=/usr/app/tls/client-keystore.p12 -Djavax.net.ssl.key-store-password=jasonrocks -Djavax.net.ssl.trust-store=/usr/app/tls/client-truststore.jks -Djavax.net.ssl.trust-store-password=jasonrocks -Djavax.net.ssl.key-store-type=PKCS12 -Djavax.net.ssl.trust-store-type=JKS -Djdk.internal.httpclient.disableHostnameVerification


