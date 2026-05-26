package com.community.service;

import com.community.entity.pojo.Article;
import com.community.entity.pojo.ArticleContent;
import com.community.entity.pojo.ArticleDraft;

/**
 * ClassName: ArticleInfoService
 * Package: com.community.service
 * Description: 文章信息服务接口
 *
 * @Author wth
 * @Create 2026/3/18 0:11
 * @Version 1.0
 */
public interface ArticleInfoService {
    
    /**
     * 发布文章
     */
    void publishArticle(String title, Integer categoryId, String content, Integer contentType, Long userId);

    /**
     * 删除文章
     */
    boolean deleteArticle(Long articleId, Long userId);

    /**
     * 更新文章
     */
    void updateArticle(Long articleId, Long userId, String title, Integer categoryId, String content, Integer contentType);

    /**
     * 根据ID查询文章详情（包含内容）
     */
    Article getArticleDetail(Long articleId);

    /**
     * 保存草稿
     */
    void saveDraft(String title, String content, Integer contentType, Long userId);

    /**
     * 发布草稿（将草稿转为正式文章）
     */
    void publishFromDraft(Long draftId, Integer categoryId, Long userId);

    /**
     * 查询用户的草稿
     */
    ArticleDraft getUserDraft(Long userId);

    /**
     * 删除草稿
     */
    boolean deleteDraft(Long draftId, Long userId);

    /**
     * 更新草稿
     */
    void updateDraft(Long draftId, Long userId, String title, String content, Integer contentType);
}
