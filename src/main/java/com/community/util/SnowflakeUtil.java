package com.community.util;

import org.springframework.stereotype.Component;

/**
 * 优化版雪花ID生成器
 * 结构：符号位(1) + 时间戳(41) + 机器ID(10) + 序列号(12)
 */
@Component
public class SnowflakeUtil {
    // ================= 配置参数 =================
    // 起始时间戳 (2026-01-01 00:00:00)
    private static final long START_TIMESTAMP = 1735660800000L;

    // 机器ID位数 (支持1024个节点)
    private final static long MACHINE_BITS = 5;   // 机器标识位数
    private final static long DATACENTER_BITS = 5; // 数据中心位数

    // 序列号位数 (每毫秒4096个)
    private static final long SEQUENCE_BITS = 12L;
    // 最大数据中心ID
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_BITS);
    // 最大机器ID
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_BITS);

    // 最大序列号 (4095)
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 左移位数
    private static final long SEQUENCE_SHIFT = 0L;
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_SHIFT = SEQUENCE_BITS + MACHINE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS + DATACENTER_BITS;

    // ================= 实例变量 =================
    private final long machineId = 1L;        // 机器ID
    private final long datacenterId = 1L;     // 数据中心ID
    private long sequence = 0L;          // 序列号
    private long lastTimestamp = -1L;    // 上次时间戳
    private long borrowedTime = 0L;      // 借调时间（处理回拨）

    // 时钟回拨容忍配置
    private static final long MAX_BORROW_TIME = 10000L;  // 最大借调10秒
    private static final long MAX_WAIT_TIME = 3000L;     // 最大等待3秒

    /**
     * 生成下一个ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 处理时钟回拨
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            return handleClockBackward(timestamp, offset);
        }

        // 处理借调时间恢复
        if (borrowedTime > 0) {
            timestamp = handleBorrowedTime(timestamp);
        }

        // 同一毫秒内生成
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 序列号用完，等待下一毫秒
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 新毫秒开始，重置序列号
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 组装ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_SHIFT)
                | (machineId << MACHINE_SHIFT)
                | (sequence << SEQUENCE_SHIFT);
    }

    /**
     * 三级时钟回拨处理机制
     */
    private long handleClockBackward(long timestamp, long offset) {
        // 1. 轻度回拨：等待追回
        if (offset <= MAX_WAIT_TIME) {
            waitForClock(offset);
            timestamp = timeGen();
            if (timestamp < lastTimestamp) {
                throw new ClockBackwardException("时钟回拨等待后仍未恢复");
            }
            return timestamp;
        }

        // 2. 重度回拨：借调未来时间
        if (offset <= MAX_BORROW_TIME) {
            borrowedTime = offset;
            // 使用上次时间戳，保证ID递增
            timestamp = lastTimestamp;
            return timestamp;
        }

        // 3. 不可恢复回拨
        throw new ClockBackwardException(
                String.format("时钟回拨过大: %dms，超过最大容忍值: %dms",
                        offset, MAX_BORROW_TIME)
        );
    }

    /**
     * 处理借调时间恢复
     */
    private long handleBorrowedTime(long timestamp) {
        if (borrowedTime > 0) {
            if (timestamp - lastTimestamp >= borrowedTime) {
                // 借调时间已还清
                borrowedTime = 0;
            } else {
                // 继续使用上次时间戳
                timestamp = lastTimestamp;
            }
        }
        return timestamp;
    }

    /**
     * 等待时钟追回
     */
    private void waitForClock(long offset) {
        try {
            Thread.sleep(offset << 1);  // 等待2倍偏移时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ClockBackwardException("时钟回拨等待被中断", e);
        }
    }

    /**
     * 等待下一毫秒
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间（可重写用于测试）
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    // 异常类
    public static class ClockBackwardException extends RuntimeException {
        public ClockBackwardException(String message) {
            super(message);
        }
        public ClockBackwardException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}