


import com.community.util.SnowflakeUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 雪花ID生成器单元测试
 */
@SpringBootTest(classes=com.community.CommunityApplication.class)
public class SnowflakeTest {
    @Resource
    private SnowflakeUtil generator;


    // 测试线程安全的SnowflakeIdGenerator
    @Test
    public void testThreadSafeIdGenerator() throws InterruptedException {
        System.out.println("\n测试线程安全的SnowflakeIdGenerator...");

        final int threadCount = 20;
        final int idPerThread = 1000;
        final Set<Long> idSet = ConcurrentHashMap.newKeySet();
        final AtomicLong duplicateCount = new AtomicLong(0);
        final AtomicLong successCount = new AtomicLong(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idPerThread; j++) {
                        long id = generator.nextId();
                        if (!idSet.add(id)) {
                            duplicateCount.incrementAndGet();
                        } else {
                            successCount.incrementAndGet();
                        }
                        Thread.sleep(0, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        int totalIds = threadCount * idPerThread;
        int uniqueIds = idSet.size();

        System.out.println("生成ID总数: " + totalIds);
        System.out.println("唯一ID数量: " + uniqueIds);
        System.out.println("重复ID数量: " + duplicateCount.get());

        assertEquals(0, duplicateCount.get(), "线程安全的生成器不应产生重复ID");
        assertEquals(totalIds, uniqueIds, "所有ID都应该是唯一的");
    }

    // 测试ID单调递增性
    @Test
    public void testIdMonotonicity() {
        System.out.println("\n测试ID单调递增性...");

        long previousId = 0L;
        int testCount = 10000;

        for (int i = 0; i < testCount; i++) {
            long id = generator.nextId();
            assertTrue(id > previousId, "ID应该单调递增");
            previousId = id;
        }

        System.out.println("测试通过: " + testCount + "个ID单调递增");
    }

    @Test
    public void testHighConcurrencyPerformance() throws InterruptedException {
        System.out.println("\n测试高并发性能...");

        final int threadCount = 50;
        final int requestsPerThread = 2000;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch finishLatch = new CountDownLatch(threadCount);
        final Set<Long> idSet = ConcurrentHashMap.newKeySet();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < requestsPerThread; j++) {
                        long id = generator.nextId();
                        idSet.add(id);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        finishLatch.await();
        long endTime = System.currentTimeMillis();

        int totalIds = threadCount * requestsPerThread;
        long timeUsed = endTime - startTime;
        double qps = totalIds * 1000.0 / timeUsed;

        System.out.println("线程数: " + threadCount);
        System.out.println("总请求数: " + totalIds);
        System.out.println("耗时: " + timeUsed + "ms");
        System.out.println("QPS: " + String.format("%.2f", qps));
        System.out.println("唯一ID数: " + idSet.size());

        assertEquals(totalIds, idSet.size(), "高并发下不应产生重复ID");
        assertTrue(qps > 10000, "QPS应达到一定水平");
    }

    // 测试ID格式
    @Test
    public void testIdFormat() {
        System.out.println("\n测试ID格式...");

        long id = generator.nextId();

        // ID应该是正数
        assertTrue(id > 0, "ID应该是正数");

        // ID应该在合理范围内
        assertTrue(id < Long.MAX_VALUE, "ID应该在Long范围内");

        // 连续生成的ID应该有递增关系
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        long id3 = generator.nextId();

        assertTrue(id2 > id1, "ID应该递增");
        assertTrue(id3 > id2, "ID应该递增");

        System.out.println("ID格式测试通过");
        System.out.println("示例ID: " + id);
        System.out.println("ID1: " + id1 + ", ID2: " + id2 + ", ID3: " + id3);
    }
}