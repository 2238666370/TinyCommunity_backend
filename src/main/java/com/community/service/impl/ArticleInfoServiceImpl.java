package com.community.service.impl;

import com.community.dao.ArticleContentDao;
import com.community.dao.ArticleDraftDao;
import com.community.dao.ArticleInfoDao;
import com.community.entity.pojo.Article;
import com.community.entity.pojo.ArticleContent;
import com.community.entity.pojo.ArticleDraft;
import com.community.entity.vo.PageResult;
import com.community.enums.ArticleContentTypeEnum;
import com.community.enums.ArticleStatusEnum;
import com.community.exception.BusinessException;
import com.community.service.ArticleInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: ArticleInfoImpl
 * Package: com.community.service.impl
 * Description: 文章信息服务实现
 *
 * @Author wth
 * @Create 2026/3/18 0:10
 * @Version 1.0
 */
@Service
@Slf4j
public class ArticleInfoServiceImpl implements ArticleInfoService {
    
    @Resource
    ArticleInfoDao articleInfoDao;
    
    @Resource
    ArticleContentDao articleContentDao;
    
    @Resource
    ArticleDraftDao articleDraftDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishArticle(String title, Integer categoryId, String content, Integer contentType, Long userId) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException("文章标题不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("文章内容不能为空");
        }
        if (!ArticleContentTypeEnum.HTML.getType().equals(contentType) 
            && !ArticleContentTypeEnum.MARKDOWN.getType().equals(contentType)) {
            throw new BusinessException("不支持的内容类型");
        }

        Article article = new Article();
        article.setTitle(title);
        article.setAuthorId(userId);
        article.setCategoryId(categoryId);
        article.setPublishTime(System.currentTimeMillis());
        article.setLastUpdateTime(System.currentTimeMillis());
        article.setStatus(ArticleStatusEnum.NORMAL.getStatus());
        article.setViewCount(0L);
        article.setLikeCount(0L);
        article.setCollectCount(0L);
        
        articleInfoDao.save(article);
        log.info("文章保存成功，文章ID为：{}", article.getId());

        ArticleContent articleContent = new ArticleContent();
        articleContent.setArticleId(article.getId());
        articleContent.setContentType(contentType);
        if (ArticleContentTypeEnum.HTML.getType().equals(contentType)) {
            articleContent.setHtmlContent(content);
        } else if (ArticleContentTypeEnum.MARKDOWN.getType().equals(contentType)) {
            articleContent.setRawContent(content);
        }
        articleContentDao.save(articleContent);
        log.info("文章内容保存成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArticle(Long articleId, Long userId) {
        Article article = articleInfoDao.getById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException("您没有权限删除此文章");
        }
        
        boolean articleDeleted = articleInfoDao.removeById(articleId);
        boolean contentDeleted = articleContentDao.deleteByArticleId(articleId);
        
        log.info("文章删除成功，文章ID：{}, 结果：{}", articleId, articleDeleted && contentDeleted);
        return articleDeleted && contentDeleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(Long articleId, Long userId, String title, Integer categoryId, String content, Integer contentType) {
        Article article = articleInfoDao.getById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException("您没有权限修改此文章");
        }

        if (title != null && !title.trim().isEmpty()) {
            article.setTitle(title);
        }
        if (categoryId != null) {
            article.setCategoryId(categoryId);
        }
        article.setLastUpdateTime(System.currentTimeMillis());
        articleInfoDao.updateById(article);

        if (content != null && !content.trim().isEmpty() && contentType != null) {
            ArticleContent articleContent = articleContentDao.getByArticleId(articleId);
            if (articleContent == null) {
                articleContent = new ArticleContent();
                articleContent.setArticleId(articleId);
            }
            articleContent.setContentType(contentType);
            if (ArticleContentTypeEnum.HTML.getType().equals(contentType)) {
                articleContent.setHtmlContent(content);
            } else if (ArticleContentTypeEnum.MARKDOWN.getType().equals(contentType)) {
                articleContent.setRawContent(content);
            }
            
            if (articleContent.getId() == null) {
                articleContentDao.save(articleContent);
            } else {
                articleContentDao.updateById(articleContent);
            }
        }
        
        log.info("文章更新成功，文章ID：{}", articleId);
    }

    @Override
    public Article getArticleDetail(Long articleId) {
        Article article = articleInfoDao.getById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        return article;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDraft(String title, String content, Integer contentType, Long userId) {
        ArticleDraft draft = articleDraftDao.getByAuthorId(userId);
        long currentTime = System.currentTimeMillis();
        
        if (draft == null) {
            draft = new ArticleDraft();
            draft.setAuthorId(userId);
            draft.setCreatedAt(currentTime);
        }
        
        if (title != null) {
            draft.setTitle(title);
        }
        if (content != null) {
            draft.setRawContent(content);
        }
        if (contentType != null) {
            draft.setContentType(contentType);
        }
        draft.setUpdatedAt(currentTime);
        
        if (draft.getId() == null) {
            articleDraftDao.save(draft);
        } else {
            articleDraftDao.updateById(draft);
        }
        log.info("草稿保存成功，用户ID：{}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishFromDraft(Long draftId, Integer categoryId, Long userId) {
        ArticleDraft draft = articleDraftDao.getDraftById(draftId);
        if (draft == null) {
            throw new BusinessException("草稿不存在");
        }
        if (!draft.getAuthorId().equals(userId)) {
            throw new BusinessException("您没有权限发布此草稿");
        }
        
        publishArticle(draft.getTitle(), categoryId, 
                      draft.getRawContent(), draft.getContentType(), userId);
        
        articleDraftDao.removeById(draftId);
        log.info("草稿发布成功，草稿ID：{}", draftId);
    }

    @Override
    public ArticleDraft getUserDraft(Long userId) {
        return articleDraftDao.getByAuthorId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDraft(Long draftId, Long userId) {
        return articleDraftDao.deleteDraft(draftId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDraft(Long draftId, Long userId, String title, String content, Integer contentType) {
        ArticleDraft draft = articleDraftDao.getDraftById(draftId);
        if (draft == null) {
            throw new BusinessException("草稿不存在");
        }
        if (!draft.getAuthorId().equals(userId)) {
            throw new BusinessException("您没有权限修改此草稿");
        }

        if (title != null && !title.trim().isEmpty()) {
            draft.setTitle(title);
        }
        if (content != null && !content.trim().isEmpty()) {
            draft.setRawContent(content);
        }
        if (contentType != null) {
            draft.setContentType(contentType);
        }
        draft.setUpdatedAt(System.currentTimeMillis());
        
        articleDraftDao.updateById(draft);
        log.info("草稿更新成功，草稿ID：{}", draftId);
    }

    @Override
    public List<Article> getArticleList(Integer categoryId) {
        java.util.List<Article> articles = articleInfoDao.selectArticleList(categoryId);
        log.info("查询文章列表成功，分类ID：{}，结果数量：{}", categoryId, articles.size());
        return articles;
    }

    @Override
    public PageResult<Article> getArticleListByPage(Integer categoryId, Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Article> pageResult = 
            articleInfoDao.selectArticleListByPage(categoryId, page, pageSize);
        
        PageResult<Article> result = new PageResult<>(
            pageResult.getRecords(),
            page,
            pageSize,
            pageResult.getTotal()
        );
        
        log.info("分页查询文章列表成功，分类ID：{}，页码：{}，每页大小：{}，总记录数：{}", 
                categoryId, page, pageSize, pageResult.getTotal());
        return result;
    }
}
