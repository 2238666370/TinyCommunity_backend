package com.community.enums;

/**
 * ClassName: ArticleStatusEnum
 * Package: com.community.enums
 * Description:
 *
 * @Author wth
 * @Create 2026/3/18 19:47
 * @Version 1.0
 */
public enum ArticleStatusEnum {
//    1-正常 2-不可见 3-禁用
    // 枚举实例（常量）
    NORMAL(1, "正常"),
    INVISIBLE(2, "不可见"),
    DISABLED(3, "禁用");
    // 状态码字段
    private Integer status;

    // 状态信息字段
    private String msg;

    // 枚举构造方法（私有）
    ArticleStatusEnum(Integer code, String msg) {
        this.status = code;
        this.msg = msg;
    }
    public Integer getStatus() {
        return status;
    }
    public String getMsg() {
        return msg;
    }
}
