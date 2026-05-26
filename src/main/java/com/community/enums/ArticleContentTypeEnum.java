package com.community.enums;

/**
 * ClassName: ArticleContentTypeEnum
 * Package: com.community.enums
 * Description:
 *
 * @Author wth
 * @Create 2026/3/19 23:49
 * @Version 1.0
 */
public enum ArticleContentTypeEnum {
    HTML(1, "html"),
    MARKDOWN(2, "markdown");
    private Integer type;
    private String msg;
    ArticleContentTypeEnum(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }
    public Integer getType() {
        return type;
    }
    public String getMsg() {
        return msg;
    }
}
