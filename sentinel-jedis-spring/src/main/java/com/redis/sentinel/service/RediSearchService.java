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

    private static final String Prefix="ticker:";


    ObjectMapper mapper = new ObjectMapper();
    private static final Set sentinels;
    static {
        sentinels = new HashSet();
    }


    @PostConstruct
    private void init() throws URISyntaxException, JsonProcessingException {
        log.info("Init RediSearchService");
        Ticker ticker = new Ticker("US", "TSLA.US", "TSLA", "D", 20230501,
                1000, (float) 100.33, (float)100.55, (float) 98.22, (float) 100.66,
                (float) 3089055.2, "openint", "mostrecent", "NYSE");
        String return_val = createTicker(ticker);
        log.info("return val from createTicker " + return_val );
    }

    public String createTicker(Ticker ticker) throws JsonProcessingException {
        String return_val = "";
        return_val = tickerRepository.create(ticker);
        return return_val;
    }


    public Ticker getKey(String keyValue) {
        Ticker returnTicker;
        returnTicker = getKeyHash(keyValue);
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
        returnValue = getFieldHash(keyValue, fieldValue);
        return returnValue;
    }

    private String getFieldHash(String keyValue, String fieldValue) {
        String returnValue = (String)stringRedisTemplate.opsForHash().get(keyValue, fieldValue);
        return returnValue;
    }

    public String setField(String key, String field, String value) {
        log.info("in service setField key " + key + " field " + field + " value " + value);
        String returnValue;
        returnValue = setFieldHash(key, field, value);
        return returnValue;
    }

    private String setFieldHash(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
        return "Done\n";
    }

    public String deleteTicker(String tickerKey) {
        log.info("in service deleteTicker tickerkey " + tickerKey);
        return  String.valueOf(stringRedisTemplate.delete(tickerKey));
    }

    public String deleteField(String keyValue, String fieldValue) {
        log.info("in service setField key " + keyValue + " field " + fieldValue );
        String returnValue;
        returnValue = deleteFieldHash(keyValue, fieldValue);
        return returnValue;
    }

    private String deleteFieldHash(String keyValue, String fieldValue) {
        return String.valueOf(stringRedisTemplate.opsForHash().delete(keyValue, fieldValue));
    }

}
