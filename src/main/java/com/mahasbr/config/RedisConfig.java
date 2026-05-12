package com.mahasbr.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.service.RegistryApplicationDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Configuration
@EnableCaching
@ConditionalOnProperty(
        name = "spring.cache.type",
        havingValue = "redis"
)
@RequiredArgsConstructor
@Slf4j
public class RedisConfig {

    private final ObjectMapper objectMapper;

    /* -------------------------------------------------
     * Redis Value Serializer (NO polymorphic typing)
     * ------------------------------------------------- */
    @Bean
    public Jackson2JsonRedisSerializer<Object> redisValueSerializer() {

        ObjectMapper redisMapper = objectMapper.copy();
        redisMapper.findAndRegisterModules();

        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(Object.class);

        serializer.setObjectMapper(redisMapper);
        return serializer;
    }

    /* -------------------------------------------------
     * Cache Manager (SAFE)
     * ------------------------------------------------- */
    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            Jackson2JsonRedisSerializer<Object> valueSerializer) {

        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .computePrefixWith(cacheName -> "v2::" + cacheName + "::")
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(valueSerializer))
                        .disableCachingNullValues()
                        .entryTtl(Duration.ofMinutes(5));

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        cacheConfigs.put("mstRegistryPages",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        cacheConfigs.put("dashboardPages",
                defaultConfig.entryTtl(Duration.ofMinutes(5)));

        cacheConfigs.put("brnDetails",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    /* -------------------------------------------------
     * Fail-safe cache handler
     * ------------------------------------------------- */
    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(
                    RuntimeException exception, Cache cache, Object key) {
            	 log.error(
                         "[CACHE] ERROR cache={} key={}",
                         cache.getName(), key, exception
                     );
            }
        };
    }
}

