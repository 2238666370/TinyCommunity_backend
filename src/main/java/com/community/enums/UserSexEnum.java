package com.community.enums;

/**
 * ClassName: UserSexEnum
 * Package: com.community.enums
 * Description:
 *
 * @Author wth
 * @Create 2026/3/14 15:33
 * @Version 1.0
 */
public enum UserSexEnum {
    // 枚举实例（常量）
    UNKOWN(2, "未知"),
    FEMAIL(1, "女"),
    MAIL(0, "男");
    // 状态码字段
    private Integer status;

    // 状态信息字段
    private String desc;

    // 枚举构造方法（私有）
    UserSexEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    public static UserRoleEnum getByStatus(Integer status) {
        for (UserRoleEnum value : UserRoleEnum.values()) {
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
