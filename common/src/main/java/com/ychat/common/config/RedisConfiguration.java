package com.ychat.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Slf4j
@Configuration
public class RedisConfiguration {

    @SuppressWarnings("all")
    @Bean
    public RedisTemplate myRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("redisTemplate");
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());//为了让它在图形化中key能看得懂

        return redisTemplate;
    }
}
