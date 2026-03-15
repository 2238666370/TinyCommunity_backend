package com.community.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章实体类
 * 对应数据库表：community.article
 *
 * @author community
 * @date 2026-03-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    /**
     * 文章主键ID（自增）
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 作者ID，关联作者表主键
     */
    private Long authorId;

    /**
     * 分类ID，关联分类表主键
     */
    private Integer categoryId;

    /**
     * 文章状态
     * 建议枚举约束：1-正常 2-不可见 3-禁用
     */
    private Integer status;

    /**
     * 发布时间（毫秒级时间戳）
     * 若数据库改为datetime类型，可替换为 java.time.LocalDateTime
     */
    private Long publishTime;

    /**
     * 最后更新时间（毫秒级时间戳）
     * 若数据库改为datetime类型，可替换为 java.time.LocalDateTime
     */
    private Long lastUpdateTime;

    /**
     * 文章阅读量
     */
    private Long viewCount;

    /**
     * 文章点赞数
     */
    private Long likeCount;

    /**
     * 文章收藏数
     */
    private Long collectCount;
}