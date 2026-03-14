package com.community.config;

import com.community.client.KeyRotationListener;
import jakarta.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;



/**
 * ClassName: RedisConfig
 * Package: com.easymeeting.redis
 * Description:
 *
 * @Author wth
 * @Create 2025/9/9 21:56
 * @Version 1.0
 */
@Configuration
public class RedisConfig<V> {
    @Resource
    private RedisConnectionFactory factory;
    @Bean("redisTemplate")
    public RedisTemplate<String, V> redisTemplate() {
        RedisTemplate<String, V> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();
        return template;
    }
}
