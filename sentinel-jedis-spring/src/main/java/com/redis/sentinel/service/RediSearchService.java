package com.redis.sentinel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

import com.redis.sentinel.domain.Ticker;
import jakarta.annotation.PostConstruct;

import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.*;

import redis.clients.jedis.json.Path2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


import java.net.URISyntaxException;
import java.util.*;
import com.redis.sentinel.repository.TickerRepository;

@Slf4j
@Service
public class RediSearchService {

    @Autowired
    private Environment env;
    @Autowired
    private TickerRepository tickerRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    String fieldPrefix = "";
    JedisPooled client;
    Jedis jedis;
    private static final Gson gson = new Gson();


    private static final String Prefix="ticker:";

    String redisHost = "localhost";
    String redisPassword = "";
    String writeJson = "false";

    ObjectMapper mapper = new ObjectMapper();
    String sentinel_port;
    String sentinel_master;
    String redis_username;
    private static final Set sentinels;
    static {
        sentinels = new HashSet();
    }


    @PostConstruct
    private void init() throws URISyntaxException {
        log.info("Init RediSearchService");
        redisHost = env.getProperty("spring.redis.sentinel_host", "localhost");
        writeJson = env.getProperty("write_json", "false");
        redisPassword = env.getProperty("spring.redis.password", "");
        sentinel_port = env.getProperty("spring.redis.sentinel_port", "8001");
        sentinel_master = env.getProperty("spring.redis.sentinel_master");
        redis_username = env.getProperty("spring.redis.username");
        log.info("redisPassword is " + redisPassword);
        sentinels.add(redisHost + ":" + sentinel_port);
        client = jedis_pooled_connection();

    }

    private JedisPooled jedis_pooled_connection() {
        // Get the configuration from the application properties/environment
        JedisPooled jedisPooled;
        JedisSentinelPool jedisSentinelPool;



        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxIdle(50);
        poolConfig.setMaxTotal(50);
        jedisSentinelPool = new JedisSentinelPool(sentinel_master, sentinels);

        HostAndPort hostAndPort = jedisSentinelPool.getCurrentHostMaster();
        // HostAndPort hostAndPort = new HostAndPort(redisHost, redisPort);
        if (redis_username!= null && !(redis_username.isEmpty())) {
            log.info( "logging in using username " + redis_username + " password " + redisPassword
                      + " redis host " + hostAndPort.getHost()
                    + " redis port " + String.valueOf(hostAndPort.getPort()));
            jedisPooled = new JedisPooled(hostAndPort.getHost(), hostAndPort.getPort(),
                    redis_username, redisPassword);
        } else if (redisPassword != null && !(redisPassword.isEmpty())) {
            String redisURL = "redis://:" + redisPassword + '@' + hostAndPort.getHost() + ':' +
                    String.valueOf(hostAndPort.getPort());
            log.info("redisURL is " + redisURL);
            jedisPooled = new JedisPooled(redisURL);
        } else {
            log.info(" no password");
            jedisPooled = new JedisPooled(hostAndPort);
        }
        return jedisPooled;
    }


    public String createTicker(Ticker ticker) throws JsonProcessingException {
        String return_val = "";
        if(writeJson.equals("true")) {
            return_val = createJSONTicker(ticker);
        } else {
            return_val = tickerRepository.create(ticker);
        }
        return return_val;
    }

    public String createJSONTicker(Ticker ticker) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String[] parts = ticker.createTickerShortGeography();
        ticker.setTickershort(parts[0]);
        ticker.setGeography(parts[1]);
        // String json = ow.writeValueAsString(ticker);
        String json = gson.toJson(ticker);
        // log.info("json is " + json);
        // log.info("id is " + ticker.createID());
        String return_val = client.jsonSet(ticker.createID(), redis.clients.jedis.json.Path2.ROOT_PATH, json);
        // log.info("return_val in createJSONTicker is " + return_val);
        return return_val;
    }

    public Ticker getKey(String keyValue) {
        Ticker returnTicker;
        if(writeJson.equals("true")) {
            returnTicker = getKeyJSON(keyValue);
        } else {
            returnTicker = getKeyHash(keyValue);
        }
        return returnTicker;
    }

    private Ticker getKeyJSON(String keyValue) {
        Ticker returnTicker = client.jsonGet(keyValue, Ticker.class);
        return returnTicker;
    }

    private Ticker getKeyHash(String keyValue) {
        Map<Object, Object> tickerHash = stringRedisTemplate.opsForHash().entries(keyValue);
        Ticker ticker = mapper.convertValue(tickerHash, Ticker.class);
        return ticker;
    }

    public String getField(String keyValue, String fieldValue) {
        log.info("in service getField key " + keyValue + " field " + fieldValue);
        String returnValue;
        if(writeJson.equals("true")) {
            returnValue = getFieldJSON(keyValue, fieldValue);
        } else {
            returnValue = getFieldHash(keyValue, fieldValue);
        }
        return returnValue;
    }

    private String getFieldJSON(String keyValue, String fieldValue) {
        Object result = client.jsonGet(keyValue, Path2.of(fieldValue));
        String returnValue = result.toString();
        return returnValue;
    }

    private String getFieldHash(String keyValue, String fieldValue) {
        String returnValue = (String)stringRedisTemplate.opsForHash().get(keyValue, fieldValue);
        return returnValue;
    }

    public String setField(String key, String field, String value) {
        log.info("in service setField key " + key + " field " + field + " value " + value);
        String returnValue;
        if(writeJson.equals("true")) {
            returnValue = setFieldJSON(key, field, value);
        } else {
            returnValue = setFieldHash(key, field, value);
        }
        return returnValue;
    }

    private String setFieldHash(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
        return "Done\n";
    }

    private String setFieldJSON(String key, String field, String value) {
        //  this replaces the object
        //  Object result = client.jsonSet(key, Path2.ROOT_PATH, new JSONArray(new String[] {field + ':' + value}));
        Object result = client.jsonSet(key, Path2.of(field), value);
        String returnValue = result.toString();
        return returnValue;
    }

    public String deleteTicker(String tickerKey) {
        log.info("in service deleteTicker tickerkey " + tickerKey);
        return  String.valueOf(stringRedisTemplate.delete(tickerKey));
    }

    public String deleteField(String keyValue, String fieldValue) {
        log.info("in service setField key " + keyValue + " field " + fieldValue );
        String returnValue;
        if(writeJson.equals("true")) {
            returnValue = deleteFieldJSON(keyValue, fieldValue);
        } else {
            returnValue = deleteFieldHash(keyValue, fieldValue);
        }
        return returnValue;
    }

    private String deleteFieldHash(String keyValue, String fieldValue) {
        return String.valueOf(stringRedisTemplate.opsForHash().delete(keyValue, fieldValue));
    }

    private String deleteFieldJSON(String keyValue, String fieldValue) {
        Object result = client.jsonDel(keyValue, Path2.of(fieldValue));
        return result.toString();
    }
}
