package com.community.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.dao.mapper.ArticleDraftMapper;
import com.community.entity.pojo.ArticleDraft;
import org.springframework.stereotype.Repository;

/**
 * ClassName: ArticleDraftDao
 * Package: com.community.dao
 * Description: 文章草稿DAO层
 *
 * @Author wth
 * @Create 2026/5/27
 * @Version 1.0
 */
@Repository
public class ArticleDraftDao extends ServiceImpl<ArticleDraftMapper, ArticleDraft> {

    /**
     * 根据作者ID查询草稿列表
     */
    public ArticleDraft getByAuthorId(Long authorId) {
        LambdaQueryWrapper<ArticleDraft> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDraft::getAuthorId, authorId)
               .orderByDesc(ArticleDraft::getUpdatedAt);
        return this.getOne(wrapper);
    }

    /**
     * 根据草稿ID查询
     */
    public ArticleDraft getDraftById(Long draftId) {
        return this.getById(draftId);
    }

    /**
     * 删除草稿
     */
    public boolean deleteDraft(Long draftId, Long authorId) {
        LambdaQueryWrapper<ArticleDraft> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDraft::getId, draftId)
               .eq(ArticleDraft::getAuthorId, authorId);
        return this.remove(wrapper);
    }
}
