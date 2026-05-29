package com.community.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.dao.mapper.ArticleInfoMapper;
import com.community.entity.pojo.Article;
import com.community.enums.ArticleStatusEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ClassName: ArticleInfoDao
 * Package: com.community.dao
 * Description:
 *
 * @Author wth
 * @Create 2026/3/18 0:24
 * @Version 1.0
 */
@Repository
public class ArticleInfoDao extends ServiceImpl<ArticleInfoMapper, Article> {

    /**
     * 查询文章列表
     * @param categoryId 分类ID，为空则不限制分类
     * @return 文章列表
     */
    public List<Article> selectArticleList(Integer categoryId) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        
        if (categoryId != null) {
            queryWrapper.eq(Article::getCategoryId, categoryId);
        }
        
        queryWrapper.eq(Article::getStatus, ArticleStatusEnum.NORMAL.getStatus())
                   .orderByDesc(Article::getPublishTime);
        
        return this.list(queryWrapper);
    }

    /**
     * 分页查询文章列表（使用 offset + limit）
     * @param categoryId 分类ID，为空则不限制分类
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public Page<Article> selectArticleListByPage(Integer categoryId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        
        if (categoryId != null) {
            queryWrapper.eq(Article::getCategoryId, categoryId);
        }
        
        queryWrapper.eq(Article::getStatus, ArticleStatusEnum.NORMAL.getStatus())
                   .orderByDesc(Article::getPublishTime);
        
        Page<Article> pageParam = new Page<>(page, pageSize);
        return this.page(pageParam, queryWrapper);
    }

}
