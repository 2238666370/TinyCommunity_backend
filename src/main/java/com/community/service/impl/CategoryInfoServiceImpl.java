package com.community.service.impl;

import com.community.constant.RedisConstant;
import com.community.dao.CategoryInfoDao;
import com.community.dao.mapper.CategoryInfoMapper;
import com.community.entity.pojo.CategoryInfo;
import com.community.entity.vo.CategoryVO;
import com.community.service.CategoryInfoService;
import com.community.util.JsonUtil;
import com.community.util.RedisUtils;
import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: CategoryInfoServiceImpl
 * Package: com.community.service.impl
 * Description:
 *
 * @Author wth
 * @Create 2026/3/18 17:57
 * @Version 1.0
 */
@Service
public class CategoryInfoServiceImpl implements CategoryInfoService {
    @Resource
    private CategoryInfoDao categoryInfoDao;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ObjectMapper objectMapper;
    @Override
    public List<CategoryVO> getCategories() {
        String json = (String) redisUtils.get(RedisConstant.CATEGORY_KEY_PREFIX);
        if(json != null && !json.isEmpty()){
            try {
                List<CategoryVO> categories = JsonUtil.fromJson(json,
                        new TypeReference<List<CategoryVO>>(){}
                );
                return categories;
            } catch (JsonProcessingException e) {
                Log.error("getCategories error:{}",e.getMessage(),e);
            }
        }
        List<CategoryInfo> categoryInfoList = categoryInfoDao.list();
        Map<Integer,CategoryVO> categoryInfoMap = categoryInfoList.stream()
                .collect(Collectors.toMap(CategoryInfo::getId, categoryInfo -> {
                    CategoryVO categoryVO = new CategoryVO(categoryInfo,new ArrayList<>());
                    return categoryVO;
                }));
        List<CategoryVO> categories = new ArrayList<>();
        for(CategoryInfo categoryInfo : categoryInfoList){
            if(categoryInfo.getParentId() == 0){
                CategoryVO categoryVO = categoryInfoMap.get(categoryInfo.getId());
                categories.add(categoryVO);
            }else{
                CategoryVO parentCategoryVO = categoryInfoMap.get(categoryInfo.getParentId());
                parentCategoryVO.getChildren().add(categoryInfoMap.get(categoryInfo.getId()));
            }
        }
        redisUtils.set(RedisConstant.CATEGORY_KEY_PREFIX,JsonUtil.toJsonSilently(categories));
        return categories;
    }
}
