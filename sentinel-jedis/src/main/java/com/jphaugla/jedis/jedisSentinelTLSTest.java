package com.jphaugla.jedis;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.HashSet;
import java.util.Set;

public class jedisSentinelTLSTest {

    public static void main(String[] args) throws GeneralSecurityException, IOException {


        String trust_store = System.getenv("TRUSTSTORE_LOCATION");
        String key_store = System.getenv("KEYSTORE_LOCATION");
        String trust_store_pw = System.getenv("TRUSTSTORE_PASSWORD");
        String key_store_pw = System.getenv("KEYSTORE_PASSWORD");
        String redis_username = System.getenv("REDIS_USERNAME");
        String redis_pw = System.getenv("REDIS_PASSWORD");
        String sentinel_host = System.getenv("SENTINEL_HOST");
        String sentinel_port = System.getenv("SENTINEL_PORT");
        String sentinel_master = System.getenv("SENTINEL_MASTER");
        Set<HostAndPort> sentinels = new HashSet<>();
        Set<String> sentinel_string = new HashSet<String>();
        HostAndPort sentinel_host_port = new HostAndPort(sentinel_host, Integer.parseInt(sentinel_port));
        sentinels.add(sentinel_host_port);
        sentinel_string.add(sentinel_host_port.toString());
        SSLSocketFactory sslFactory = createSslSocketFactory(
                trust_store,
                trust_store_pw, // use the password you specified for keytool command
                key_store,
                key_store_pw // use the password you specified for openssl command
        );

        JedisClientConfig redisConfig = DefaultJedisClientConfig.builder()
                .ssl(true).sslSocketFactory(sslFactory)
                .user(redis_username) // use your Redis user. More info https://redis.io/docs/management/security/acl/
                .password(redis_pw) // use your Redis password
                .build();
        JedisClientConfig sentinelConfig = DefaultJedisClientConfig.builder()
                .ssl(true).sslSocketFactory(sslFactory)
                .build();
        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();

        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(sentinel_master, sentinels, poolConfig, redisConfig, sentinelConfig);
        Jedis jedis =jedisSentinelPool.getResource();
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