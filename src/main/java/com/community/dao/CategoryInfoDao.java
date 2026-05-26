package com.community.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.dao.mapper.CategoryInfoMapper;
import com.community.entity.pojo.CategoryInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * ClassName: CategoryInfoMapperImpl
 * Package: com.community.dao
 * Description:
 *
 * @Author wth
 * @Create 2026/3/18 17:54
 * @Version 1.0
 */
@Repository
public class CategoryInfoDao extends ServiceImpl<CategoryInfoMapper, CategoryInfo> {
}
