package com.community.enums;

public enum ResponseCodeEnum {
    // 枚举实例（常量）
    CODE_200(200, "请求成功"),
    CODE_404(404, "请求地址不能存在"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已经存在"),
    CODE_603(603, "数据转换失败"),
    CODE_500(500, "服务器错误，请联系管理员"),
    CODE_901(901, "登录超时，请重新登录");
    // 状态码字段
    private Integer code;

    // 状态信息字段
    private String msg;

    // 枚举构造方法（私有）
    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
