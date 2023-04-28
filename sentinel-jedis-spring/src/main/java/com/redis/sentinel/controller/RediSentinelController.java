package com.redis.sentinel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.sentinel.domain.Ticker;
import com.redis.sentinel.service.RediSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import redis.clients.jedis.search.SearchResult;


import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;


@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/")
@RestController
@Configuration

public class RediSentinelController implements WebMvcConfigurer {

    @Inject
    RediSearchService rediSearchService;
    ObjectMapper mapper = new ObjectMapper();


    @GetMapping("/status")
    public String status() {
        return "OK";
    }


    @GetMapping("/key")
    public Ticker getKey(
            @RequestParam(name="keyValue")String keyValue){
        return rediSearchService.getKey(keyValue);
    }

    @GetMapping("/field")
    public String getField(
            @RequestParam(name="keyValue")String keyValue,
            @RequestParam(name="fieldValue")String fieldValue){
        log.info("in controller getField");
        return rediSearchService.getField(keyValue, fieldValue);
    }

    @PostMapping("/postTicker")
    public String postTicker(@RequestBody Ticker ticker) throws IOException {
        // log.info("in uploadCSVFile");
        return rediSearchService.createTicker(ticker);
    }

    @PutMapping("/setfield")
    public String setField(@RequestParam String key,
                           @RequestParam String field,
                           @RequestParam String value) {
        return rediSearchService.setField(key, field, value);
    }


    @DeleteMapping("/key")
    public String deleteTicker (@RequestParam String keyValue) {

        return rediSearchService.deleteTicker(keyValue);
    }

    @DeleteMapping("/field")
    public String deleteField(@RequestParam String keyValue,
                              @RequestParam String fieldValue) {

        return rediSearchService.deleteField(keyValue, fieldValue);
    }

}
