package com.community.entity.pojo;

/**
 * ClassName: UserContext
 * Package: com.community.entity.pojo
 * Description:
 *
 * @Author wth
 * @Create 2026/3/15 13:00
 * @Version 1.0
 */
// 创建UserContext类来管理ThreadLocal
public class UserContext {
    private static final ThreadLocal<UserInfo> USER_INFO_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserInfo(UserInfo userInfo) {
        USER_INFO_THREAD_LOCAL.set(userInfo);
    }

    public static UserInfo getUserInfo() {
        return USER_INFO_THREAD_LOCAL.get();
    }

    public static Integer getRole() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null ? userInfo.getRole() : null;
    }

    public static Long getUserId() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    public static String getUsername() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null ? userInfo.getUserName() : null;
    }

    public static void clear() {
        USER_INFO_THREAD_LOCAL.remove();
    }
}