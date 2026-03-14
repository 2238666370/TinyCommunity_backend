package com.community.util;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: RedisUtils
 *
 * Description:
 *
 * @Author wth
 * @Create 2025/9/9 22:16
 * @Version 1.0
 */
@Component("redisUtils")
@Slf4j
public class RedisUtils<V> {

    @Resource
    private RedisTemplate<String, V> redisTemplate;

    // Lua脚本：原子性释放锁（只有锁的持有者才能删除）
    private static final String UNLOCK_LUA_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "    return redis.call('del', KEYS[1]) " +
                    "else " +
                    "    return 0 " +
                    "end";

    // Lua脚本：原子性获取锁并设置过期时间
    private static final String LOCK_LUA_SCRIPT =
            "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
                    "    return redis.call('pexpire', KEYS[1], ARGV[2]) " +
                    "else " +
                    "    return 0 " +
                    "end";

    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    public V get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }


    public boolean set(String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置redisKey:{}, value:{} 失败", key, value, e);
            return false;
        }
    }


    public boolean setex(String key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置redisKey:{}, value:{} 失败", key, value, e);
            return false;
        }
    }


    public void hset(String key, String hashKey, V value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }


    public V hget(String key, String hashKey) {
        return (V) redisTemplate.opsForHash().get(key, hashKey);
    }

    public void hdel(String key, String... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }


    public List<V> hvals(String key) {
        return (List<V>) redisTemplate.opsForHash().values(key);
    }



    // =================== 分布式锁方法 ===================

    /**
     * 尝试获取分布式锁（非阻塞）
     *
     * @param lockKey     锁的key
     * @param lockValue   锁的唯一标识（建议使用UUID）
     * @param expireTime  锁的过期时间（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, V lockValue, long expireTime) {
        if (!StringUtils.hasText(lockKey) || lockValue == null || expireTime <= 0) {
            log.warn("锁参数不合法: lockKey={}, lockValue={}, expireTime={}", lockKey, lockValue, expireTime);
            return false;
        }

        try {
            // 使用Lua脚本保证原子性：setnx + pexpire
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(LOCK_LUA_SCRIPT, Long.class);
            Long result = redisTemplate.execute(script,
                    Collections.singletonList(lockKey),
                    lockValue,
                    expireTime);

            return result != null && result == 1;
        } catch (Exception e) {
            log.error("获取分布式锁失败, lockKey: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 尝试获取分布式锁（阻塞，可设置超时时间）
     *
     * @param lockKey         锁的key
     * @param lockValue       锁的唯一标识（建议使用UUID）
     * @param expireTime      锁的过期时间（毫秒）
     * @param acquireTimeout  获取锁的超时时间（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, V lockValue, long expireTime, long acquireTimeout) {
        if (!StringUtils.hasText(lockKey) || lockValue == null || expireTime <= 0 || acquireTimeout <= 0) {
            log.warn("锁参数不合法: lockKey={}, lockValue={}, expireTime={}, acquireTimeout={}",
                    lockKey, lockValue, expireTime, acquireTimeout);
            return false;
        }

        long endTime = System.currentTimeMillis() + acquireTimeout;

        while (System.currentTimeMillis() < endTime) {
            if (tryLock(lockKey, lockValue, expireTime)) {
                return true;
            }

            try {
                // 短暂休眠后重试
                Thread.sleep(100); // 可根据实际情况调整休眠时间
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("获取锁过程被中断, lockKey: {}", lockKey, e);
                break;
            }
        }

        return false;
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁的key
     * @param lockValue 锁的唯一标识（必须与加锁时的值一致）
     * @return 是否释放成功
     */
    public boolean unlock(String lockKey, V lockValue) {
        if (!StringUtils.hasText(lockKey) || lockValue == null) {
            log.warn("释放锁参数不合法: lockKey={}, lockValue={}", lockKey, lockValue);
            return false;
        }

        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_LUA_SCRIPT, Long.class);
            Long result = redisTemplate.execute(script,
                    Collections.singletonList(lockKey),
                    lockValue);

            return result != null && result == 1;
        } catch (Exception e) {
            log.error("释放分布式锁失败, lockKey: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 获取分布式锁（简化版，自动生成锁标识）
     *
     * @param lockKey     锁的key
     * @param expireTime  锁的过期时间（毫秒）
     * @return 锁的唯一标识（解锁时需要使用），获取失败返回null
     */
    @SuppressWarnings("unchecked")
    public String acquireLock(String lockKey, long expireTime) {
        String lockValue = UUID.randomUUID().toString();
        boolean success = tryLock(lockKey, (V) lockValue, expireTime);
        return success ? lockValue : null;
    }

    /**
     * 获取分布式锁（阻塞版，自动生成锁标识）
     *
     * @param lockKey         锁的key
     * @param expireTime      锁的过期时间（毫秒）
     * @param acquireTimeout  获取锁的超时时间（毫秒）
     * @return 锁的唯一标识（解锁时需要使用），获取失败返回null
     */
    @SuppressWarnings("unchecked")
    public String acquireLock(String lockKey, long expireTime, long acquireTimeout) {
        String lockValue = UUID.randomUUID().toString();
        boolean success = tryLock(lockKey, (V) lockValue, expireTime, acquireTimeout);
        return success ? lockValue : null;
    }

    // =================== 自增方法 ===================

    /**
     * 原子自增
     * @param key 键
     * @param delta 增量（可为负数）
     * @return 自增后的值
     */
    public Long increment(String key, long delta) {
        if (key == null || delta == 0) {
            log.warn("自增参数不合法: key={}, delta={}", key, delta);
            return null;
        }
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis自增失败, key: {}", key, e);
            return null;
        }
    }

    /**
     * 原子自增1
     * @param key 键
     * @return 自增后的值
     */
    public Long increment(String key) {
        return increment(key, 1L);
    }

    /**
     * 原子自增并设置过期时间
     * @param key 键
     * @param delta 增量
     * @param expireTime 过期时间（秒）
     * @return 自增后的值
     */
    public Long increment(String key, long delta, long expireTime) {
        Long result = increment(key, delta);
        if (result != null && expireTime > 0) {
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 原子递减
     * @param key 键
     * @param delta 减量
     * @return 递减后的值
     */
    public Long decrement(String key, long delta) {
        return increment(key, -delta);
    }

    /**
     * 原子递减1
     * @param key 键
     * @return 递减后的值
     */
    public Long decrement(String key) {
        return decrement(key, 1L);
    }

    // =================== 发布订阅方法 ===================

    /**
     * 发布消息到指定频道（字符串格式）
     * @param channel 频道名称
     * @param message 消息内容
     */
    public void publish(String channel, String message) {
        if (!StringUtils.hasText(channel) || !StringUtils.hasText(message)) {
            log.warn("发布消息参数不合法: channel={}, message={}", channel, message);
            return;
        }
        try {
            redisTemplate.convertAndSend(channel, message);
            log.debug("发布消息成功, channel: {}, message: {}", channel, message);
        } catch (Exception e) {
            log.error("发布消息失败, channel: {}", channel, e);
        }
    }
}