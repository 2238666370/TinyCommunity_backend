package com.community.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.dao.mapper.ArticleContentMapper;
import com.community.entity.pojo.ArticleContent;
import org.springframework.stereotype.Repository;

/**
 * ClassName: ArticleContentDao
 * Package: com.community.dao
 * Description:
 *
 * @Author wth
 * @Create 2026/5/23 13:44
 * @Version 1.0
 */
@Repository
public class ArticleContentDao extends ServiceImpl<ArticleContentMapper, ArticleContent> {

    /**
     * 根据文章ID查询内容
     */
    public ArticleContent getByArticleId(Long articleId) {
        LambdaQueryWrapper<ArticleContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleContent::getArticleId, articleId);
        return this.getOne(wrapper);
    }

    /**
     * 根据文章ID删除内容
     */
    public boolean deleteByArticleId(Long articleId) {
        LambdaQueryWrapper<ArticleContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleContent::getArticleId, articleId);
        return this.remove(wrapper);
    }
}
