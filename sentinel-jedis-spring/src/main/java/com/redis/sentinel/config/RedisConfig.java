package com.redis.sentinel.config;

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

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@EnableAsync

public class RedisConfig {
    @Autowired
    private Environment env;


    @Bean(name = "jedisConnectionFactory")
    @Primary
    public RedisConnectionFactory jedisConnectionFactory() {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(50);
        poolConfig.setMaxTotal(50);
        JedisClientConfiguration.JedisClientConfigurationBuilder clientConfig = JedisClientConfiguration.builder();
        clientConfig.usePooling().poolConfig(poolConfig);
        JedisConnectionFactory jedisConnectionFactory;
        String host = env.getProperty("spring.redis.sentinel_host");
        Integer sentinel_port = Integer.parseInt(env.getProperty("spring.redis.sentinel_port"));
        String sentinel_master = env.getProperty("spring.redis.sentinel_master");
        String redis_password = env.getProperty("spring.redis.password");
        String redis_username = env.getProperty("spring.redis.username");

        RedisSentinelConfiguration redisServerConf = new RedisSentinelConfiguration()
                    .master(sentinel_master)
                    .sentinel(host, sentinel_port);
        if(redis_password != null && !redis_password.isEmpty()) {
            redisServerConf.setPassword(RedisPassword.of(redis_password));
        }
        if(redis_username != null && !redis_username.isEmpty()) {
            redisServerConf.setUsername(redis_username);
        }
        jedisConnectionFactory = new JedisConnectionFactory(redisServerConf, clientConfig.build());
        return jedisConnectionFactory;
    }

    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<Long>(Long.class));
        // redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
}
