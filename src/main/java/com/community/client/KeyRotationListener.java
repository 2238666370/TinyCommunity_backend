package com.community.client;

import com.community.constant.RedisConstant;
import com.community.scheduledtast.DistributedJwtSecretManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: KeyRotationListener
 * Package: com.community.client
 * Description:
 *
 * @Author wth
 * @Create 2026/3/14 21:43
 * @Version 1.0
 */
@Component
public class KeyRotationListener {

    @Resource
    private DistributedJwtSecretManager jwtSecretManager;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private final Map<String, SecretKey> localKeyCache = new ConcurrentHashMap<>();

    /**
     * 初始化消息监听
     */
    @PostConstruct
    public void init() {
        redisTemplate.getConnectionFactory().getConnection()
                .subscribe((message, pattern) -> {
                    String channel = new String(message.getChannel());
                    String newKeyId = new String(message.getBody());
                    onKeyRotated(newKeyId, channel);
                }, "jwt:key:rotated".getBytes());
    }

    /**
     * 处理密钥轮换事件
     */
    public void onKeyRotated(String newKeyId, String channel) {
        System.out.println("收到密钥轮换通知，新密钥ID: " + newKeyId + ", 通道: " + channel);

        // 1. 从Redis获取新密钥
        refreshLocalCache();

        // 2. 清理本地令牌缓存（如果有的话）
        cleanupLocalCache();
    }

    /**
     * 刷新本地密钥缓存
     */
    private void refreshLocalCache() {
        try {
            // 从分布式管理器获取最新密钥
            SecretKey currentKey = jwtSecretManager.getCurrentSecret();

            // 获取密钥ID
            String currentKeyId = getCurrentKeyIdFromRedis();

            if (currentKeyId != null && currentKey != null) {
                // 更新本地缓存
                localKeyCache.put(currentKeyId, currentKey);
                System.out.println("本地密钥缓存已更新，密钥ID: " + currentKeyId);
            }
        } catch (Exception e) {
            System.err.println("刷新本地密钥缓存失败: " + e.getMessage());
        }
    }

    /**
     * 从Redis获取当前密钥ID
     */
    public String getCurrentKeyIdFromRedis() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(RedisConstant.KEY_ID);
    }

    /**
     * 清理本地缓存
     * 这里可以根据业务需求清理过期的令牌缓存
     */
    private void cleanupLocalCache() {
        // 示例：保留最近2个密钥，删除更旧的
        if (localKeyCache.size() > 2) {
            // 这里需要根据密钥ID的时间戳或版本号来判断哪些是旧密钥
            // 实际实现需要根据具体的密钥ID生成规则来调整
            localKeyCache.entrySet().removeIf(entry ->
                    !isCurrentOrPreviousKey(entry.getKey())
            );
        }
    }

    /**
     * 判断是否为当前或上一个密钥
     */
    private boolean isCurrentOrPreviousKey(String keyId) {
        String currentKeyId = getCurrentKeyIdFromRedis();
        String previousKey = redisTemplate.opsForValue().get(RedisConstant.PREVIOUS_KEY);

        return keyId.equals(currentKeyId) ||
                (previousKey != null && keyId.equals(extractKeyIdFromEncodedKey(previousKey)));
    }

    /**
     * 从编码的密钥字符串中提取密钥ID
     * 注意：这需要与DistributedJwtSecretManager中的存储方式对应
     */
    private String extractKeyIdFromEncodedKey(String encodedKey) {
        // 这里需要根据实际存储方式实现
        // 如果encodedKey只存密钥值，不存ID，则需要额外逻辑
        return null; // 需要根据实际实现调整
    }

    /**
     * 获取本地缓存的密钥
     */
    public SecretKey getKeyFromCache(String keyId) {
        return localKeyCache.get(keyId);
    }

    /**
     * 获取当前密钥（先从本地缓存，没有再查Redis）
     */
    public SecretKey getCurrentKeyWithCache() {
        String currentKeyId = getCurrentKeyIdFromRedis();
        if (currentKeyId == null) {
            return jwtSecretManager.getCurrentSecret();
        }

        SecretKey cachedKey = localKeyCache.get(currentKeyId);
        if (cachedKey != null) {
            return cachedKey;
        }

        // 缓存未命中，从管理器获取并更新缓存
        SecretKey currentKey = jwtSecretManager.getCurrentSecret();
        localKeyCache.put(currentKeyId, currentKey);
        return currentKey;
    }
}