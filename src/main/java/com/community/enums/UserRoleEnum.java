package com.community.enums;

/**
 * ClassName: UserRoleEnum
 * Package: com.community.enums
 * Description:
 *
 * @Author wth
 * @Create 2026/3/14 11:17
 * @Version 1.0
 */
public enum UserRoleEnum {
    // 枚举实例（常量）
    NORMAL(2, "普通用户"),
    SPECIAL(1, "VIP"),
    ADMIN(0, "管理员");
    // 状态码字段
    private Integer status;

    // 状态信息字段
    private String desc;

    // 枚举构造方法（私有）
    UserRoleEnum(Integer status, String desc) {
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
