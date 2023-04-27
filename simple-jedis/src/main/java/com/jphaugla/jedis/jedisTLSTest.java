package com.jphaugla.jedis;
import redis.clients.jedis.*;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class jedisTLSTest {

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        HostAndPort address = new HostAndPort("redis", 6379);

        String trust_store = System.getenv("TRUSTSTORE_LOCATION");
        String key_store = System.getenv("KEYSTORE_LOCATION");
        String trust_store_pw = System.getenv("TRUSTSTORE_PASSWORD");
        String key_store_pw = System.getenv("KEYSTORE_PASSWORD");
        String redis_username = System.getenv("REDIS_USERNAME");
        String redis_pw = System.getenv("REDIS_PASSWORD");
        SSLSocketFactory sslFactory = createSslSocketFactory(
                trust_store,
                trust_store_pw, // use the password you specified for keytool command
                key_store,
                key_store_pw // use the password you specified for openssl command
        );

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .ssl(true).sslSocketFactory(sslFactory)
                .user(redis_username) // use your Redis user. More info https://redis.io/docs/management/security/acl/
                .password(redis_pw) // use your Redis password
                .build();

        JedisPooled jedis = new JedisPooled(address, config);
        jedis.set("foo", "bar");
        System.out.println(jedis.get("foo")); // prints bar
    }

    private static SSLSocketFactory createSslSocketFactory(
            String caCertPath, String caCertPassword, String userCertPath, String userCertPassword)
            throws IOException, GeneralSecurityException {

        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(new FileInputStream(userCertPath), userCertPassword.toCharArray());

        KeyStore trustStore = KeyStore.getInstance("jks");
        trustStore.load(new FileInputStream(caCertPath), caCertPassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
        trustManagerFactory.init(trustStore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
        keyManagerFactory.init(keyStore, userCertPassword.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }
}