package com.community.enums;

/**
 * ClassName: UserStatus
 * Package: com.easymeeting.enums
 * Description:
 *
 * @Author wth
 * @Create 2025/9/10 2:10
 * @Version 1.0
 */
public enum UserStatusEnum {
    // 枚举实例（常量）
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");
    // 状态码字段
    private Integer status;

    // 状态信息字段
    private String desc;

    // 枚举构造方法（私有）
    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    public static UserStatusEnum getByStatus(Integer status) {
        for (UserStatusEnum value : UserStatusEnum.values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
    public Integer getStatus() {
        return status;
    }
    public String getDesc() {
        return desc;
    }
}
