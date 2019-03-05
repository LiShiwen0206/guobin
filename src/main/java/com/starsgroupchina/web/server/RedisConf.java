package com.starsgroupchina.web.server;

import com.google.common.collect.Maps;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Created by zhangfeng on 2018-5-31.
 */
@Configuration
public class RedisConf {

    public static final String REDIS_TEMPLATE = "REDIS_TEMPLATE";

    @Bean(name = REDIS_TEMPLATE)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        template.setConnectionFactory(factory);
        return template;
    }

    /**
     * redis cache
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        //     Map map = Maps.newConcurrentMap();
//        map.put(KeyCache.Key.ROOMS, 180L);
//        map.put(KeyCache.Key.VIDEOS, 60L);
//        map.put(KeyCache.Key.SORT_ROOMS, 15L);
//        map.put(KeyCache.Key.SORT_VIDEO, 15L);
//        map.put(KeyCache.Key.SEARCH_ROOMS, 30L);
//        map.put(KeyCache.Key.ACTIVITY_VOTES, 30L);
        Map<String, RedisCacheConfiguration> cacheConfigurations = Maps.newHashMap();
       //cacheConfigurations.put(Const.ACHIEVEMENT, RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(60 * 5)));
//        cacheConfigurations.put(Const.DAYAUDIT, RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(60 * 5)));
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(60*60)))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();

        return cacheManager;
    }
}
