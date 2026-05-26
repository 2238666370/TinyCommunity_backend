package com.community.dao;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.dao.mapper.ArticleInfoMapper;
import com.community.entity.pojo.Article;
import com.community.entity.pojo.ArticleContent;
import org.springframework.stereotype.Repository;

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

}
