package com.community.scheduledtast;

import com.community.constant.RedisConstant;
import com.community.util.RedisUtils;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: DistributedJwtSecretManager
 * Package: com.community.util
 * Description:
 *
 * @Author wth
 * @Create 2026/3/14 21:45
 * @Version 1.0
 */
@Component
@Slf4j
public class DistributedJwtSecretManager {



    @Resource
    private RedisUtils<String> redisUtils;


    private final Lock localLock = new ReentrantLock();

    /**
     * 获取当前密钥
     */
    public SecretKey getCurrentSecret() {
        String keyStr = redisUtils.get(RedisConstant.CURRENT_KEY);
        if (keyStr != null) {
            return decodeKey(keyStr);
        }
        return generateAndStoreNewKey();
    }

    /**
     * 生成并存储新密钥
     */
    private SecretKey generateAndStoreNewKey() {
        String lockValue = null;
        try {
            // 获取分布式锁，超时时间5秒，等待时间10秒
            lockValue = redisUtils.acquireLock(RedisConstant.ROTATION_LOCK, 10000, 5000);
            log.info("获取分布式锁成功，开始生成新密钥");

            if (lockValue != null) {
                // 双重检查
                String existingKey = redisUtils.get(RedisConstant.CURRENT_KEY);
                if (existingKey != null) {
                    return decodeKey(existingKey);
                }

                // 生成新密钥
                SecretKey newKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                String newKeyId = generateKeyId();
                log.info("生成新密钥，密钥ID: " + newKeyId);
                // 保存到Redis
                redisUtils.setex(RedisConstant.CURRENT_KEY, encodeKey(newKey), 26 * 60 * 60); // 26小时过期
                redisUtils.set(RedisConstant.KEY_ID, newKeyId);
                redisUtils.set(RedisConstant.LAST_ROTATION, LocalDateTime.now().toString());
                log.info("保存密钥成功，密钥ID: " + newKeyId);
                return newKey;
            }
        } finally {
            // 释放锁
            if (lockValue != null) {
                redisUtils.unlock(RedisConstant.ROTATION_LOCK, lockValue);
            }
        }
        throw new RuntimeException("获取密钥失败");
    }

    /**
     * 定时密钥轮换
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void distributedKeyRotation() {
        String lockValue = null;
        try {
            // 获取分布式锁，30秒过期，不等待（0秒超时）
            lockValue = redisUtils.acquireLock(RedisConstant.ROTATION_SCHEDULED_LOCK, 30000, 0);

            if (lockValue != null) {
                // 检查是否需要轮换
                if (shouldRotateKey()) {
                    performKeyRotation();
                }
            }
        } finally {
            // 释放锁
            if (lockValue != null) {
                redisUtils.unlock(RedisConstant.ROTATION_SCHEDULED_LOCK, lockValue);
            }
        }
    }

    /**
     * 执行密钥轮换
     */
    private synchronized void performKeyRotation() {
        // 1. 将当前密钥保存为上一个密钥
        String currentKey = redisUtils.get(RedisConstant.CURRENT_KEY);
        if (currentKey != null) {
            redisUtils.setex(RedisConstant.PREVIOUS_KEY, currentKey, 2 * 60 * 60); // 2小时过期
        }

        // 2. 生成新密钥
        SecretKey newKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String newKeyId = generateKeyId();

        // 3. 更新当前密钥
        redisUtils.setex(RedisConstant.CURRENT_KEY, encodeKey(newKey), 26 * 60 * 60); // 26小时过期
        redisUtils.set(RedisConstant.KEY_ID, newKeyId);
        redisUtils.set(RedisConstant.LAST_ROTATION, LocalDateTime.now().toString());

        // 4. 发布密钥更新事件
        redisUtils.publish("jwt:key:rotated", newKeyId);

        // 5. 记录日志
        System.out.println("分布式密钥轮换完成，新密钥ID: " + newKeyId);
    }

    /**
     * 检查是否需要轮换密钥
     */
    private boolean shouldRotateKey() {
        String lastRotation = redisUtils.get(RedisConstant.LAST_ROTATION);
        if (lastRotation == null) {
            return true;
        }

        LocalDateTime lastTime = LocalDateTime.parse(lastRotation);
        return lastTime.plusHours(24).isBefore(LocalDateTime.now());
    }

    // 编解码方法
    private String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    private SecretKey decodeKey(String encoded) {
        byte[] keyBytes = Base64.getDecoder().decode(encoded);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateKeyId() {
        // 使用 RedisUtils 的自增功能
        log.info("开始生成密钥ID");
        Long counter = redisUtils.increment(RedisConstant.JWT_KEY_COUNTER);
        log.info("生成密钥ID成功，密钥ID: " + counter);
        return "kid_" + System.currentTimeMillis() + "_" + (counter != null ? counter : 0);
    }
}