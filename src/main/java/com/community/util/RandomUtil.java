package com.community.util;

import java.util.Random;
import java.util.UUID;

/**
 * ClassName: RandomUtil
 * Package: com.community.util
 * Description:
 *
 * @Author wth
 * @Create 2026/3/14 15:27
 * @Version 1.0
 */
public class RandomUtil {
    private static final Random random = new Random();
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    public static Integer generateNumberLowerThan10() {
        return random.nextInt(10);
    }

}
