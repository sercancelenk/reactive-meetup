package com.turkcell.reactive.meetup.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkcell.reactive.meetup.model.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisService {
    private final ObjectMapper objectMapper;
    private final ReactiveRedisTemplate<String,String> reactiveRedisTemplate;

    public Mono<Comment> save(long videoId,Comment comment){
        try{
            return reactiveRedisTemplate.opsForSet().add("comment." + videoId,objectMapper.writeValueAsString(comment))
                    .map(any -> comment);
        }catch(Exception e){
            return Mono.error(new RuntimeException(e));
        }
    }
}
