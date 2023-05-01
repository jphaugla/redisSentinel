package com.redis.jedispring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
@Slf4j
@EnableConfigurationProperties(RedisProperties.class)
@EnableAsync

public class RedisConfig {
    @Autowired
    private Environment env;

    @Bean
    @Primary
    public SSLSocketFactory sslSocketFactory() throws IOException, KeyStoreException, NoSuchAlgorithmException,
            CertificateException, UnrecoverableKeyException, KeyManagementException {
        String trust_store = System.getenv("TRUSTSTORE_LOCATION");
        String key_store = System.getenv("KEYSTORE_LOCATION");
        String trust_store_pw = System.getenv("TRUSTSTORE_PASSWORD");
        String key_store_pw = System.getenv("KEYSTORE_PASSWORD");
        log.info("trust location=" + trust_store);
        log.info("key location=" + key_store);
        log.info("trust pw=" + trust_store_pw);
        log.info("key pw=" + key_store_pw);
        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(new FileInputStream(key_store), key_store_pw.toCharArray());

        KeyStore trustStore = KeyStore.getInstance("jks");
        trustStore.load(new FileInputStream(trust_store), trust_store_pw.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
        trustManagerFactory.init(trustStore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
        keyManagerFactory.init(keyStore, key_store_pw.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    @Bean(name = "jedisConnectionFactory")
    @Primary
    public RedisConnectionFactory jedisConnectionFactory(SSLSocketFactory sslSocketFactory)
            throws UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(50);
        poolConfig.setMaxTotal(50);

        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().useSsl()
                .sslSocketFactory(sslSocketFactory).and().usePooling().poolConfig(poolConfig).build();

        JedisConnectionFactory jedisConnectionFactory;
        String host = env.getProperty("spring.redis.host");
        Integer port = Integer.parseInt(env.getProperty("spring.redis.port"));
        String redis_password = env.getProperty("spring.redis.password");
        String redis_username = env.getProperty("spring.redis.username");
        Boolean use_tls = Boolean.parseBoolean(env.getProperty("spring.redis.ssl","false"));

        log.info("parameters  host " + host + " port " + port + " redis username " + redis_username + " redis password "
                    + redis_password + " use tls " + Boolean.valueOf(use_tls));
        RedisStandaloneConfiguration redisServerConf = new RedisStandaloneConfiguration();
        redisServerConf.setHostName(host);
        redisServerConf.setPort(port);
        if(redis_password != null && !redis_password.isEmpty()) {
            redisServerConf.setPassword(RedisPassword.of(redis_password));
        }
        if(redis_username != null && !redis_username.isEmpty()) {
            redisServerConf.setUsername(redis_username);
        }

        jedisConnectionFactory = new JedisConnectionFactory(redisServerConf, jedisClientConfiguration);

        return jedisConnectionFactory;
    }

    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate() throws UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory(sslSocketFactory()));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<Long>(Long.class));
        // redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    @Bean
    public StringRedisTemplate stringRedisTemplate() throws UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory(sslSocketFactory()));
        return redisTemplate;
    }
}
