package com.community.entity.vo;

public class ResponseVO<T> {
    private String status;   // 状态描述（如："success", "error"）
    private Integer code;    // 状态码（如：200, 500）
    private String info;     // 附加信息（如：详细的说明消息）
    private T data;          // 泛型数据载体（可承载任意类型的业务数据）

    // 以下是各字段的标准getter和setter方法
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
