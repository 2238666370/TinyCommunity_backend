package com.community.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章草稿实体类
 * 对应数据库表：article_draft
 *
 * @author community
 * @date 2026-05-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDraft {

    /**
     * 草稿主键ID
     */
    private Long id;

    /**
     * 文章ID（发布后关联）
     */
    private Long articleId;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 内容类型：1-HTML 2-Markdown
     */
    private Integer contentType;

    /**
     * 原始内容（Markdown）
     */
    private String rawContent;

    /**
     * HTML内容
     */
    private String htmlContent;

    /**
     * 保存类型
     */
    private Integer saveType;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createdAt;

    /**
     * 更新时间（毫秒时间戳）
     */
    private Long updatedAt;
}
