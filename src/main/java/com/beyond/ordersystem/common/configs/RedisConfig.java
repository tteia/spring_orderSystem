package com.beyond.ordersystem.common.configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    // application.yml 의 spring.redis.host 의 정보를 가져옴.
    @Value("${spring.redis.host}")
    public String host;

    @Value("${spring.redis.port}")
    public int port;

    @Bean
    // RedisConnectionFactory 는 Redis 서버와의 연결을 설정하는 역할.
    // LettuceConnectionFactory 는 RedisConnectionFactory 의 구현체로서 실질적인 역할 수행.
    public RedisConnectionFactory redisConnectionFactory(){
//        return new LettuceConnectionFactory(host, port);
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
//        configuration.setDatabase(2);
//        configuration.setPassword("1234");
        return new LettuceConnectionFactory(configuration);
    }

    // redisTemplate 은 redis 와 상호작용할 때 redis key, value 의 형식을 정의.
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
//    redisTemplate.opsForValue().set(key, value);
//    redisTemplate.opsForValue().get(key);
//    redisTemplate.opsForValue().increment() 또는 decrement
}
