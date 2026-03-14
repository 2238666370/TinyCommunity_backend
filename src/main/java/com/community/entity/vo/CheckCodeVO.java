package com.community.entity.vo;

/**
 * ClassName: CheckCodeVO
 * Package: com.community.entity.vo
 * Description:
 *
 * @Author wth
 * @Create 2026/3/11 20:17
 * @Version 1.0
 */
public class CheckCodeVO {
    private String captcha;
    private String key;
    private long timestamp;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
