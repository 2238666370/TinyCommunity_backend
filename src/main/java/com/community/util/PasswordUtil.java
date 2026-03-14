package com.community.util;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * ClassName: PasswordUtil
 * Package: com.community.util
 * Description:
 *
 * @Author wth
 * @Create 2026/3/14 15:21
 * @Version 1.0
 */
public class PasswordUtil {
    public static boolean checkPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        int numCount = 0;
        int smallLetterCount = 0;
        int bigLetterCount = 0;
        int otherCount = 0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c >= '0' && c <= '9') {
                numCount++;
            } else if (c >= 'a' && c <= 'z') {
                smallLetterCount++;
            } else if (c >= 'A' && c <= 'Z') {
                bigLetterCount++;
            } else {
                otherCount++;
            }
        }
        int count = 0;
        if (numCount > 0) {
            count++;
        }
        if (smallLetterCount > 0) {
            count++;
        }
        if (bigLetterCount > 0) {
            count++;
        }
        if (otherCount > 0) {
            count++;
        }
        return count >= 2;
    }
}
