package com.community.service;

import com.community.entity.vo.CategoryVO;

import java.util.List;

/**
 * ClassName: CategoryInfoService
 * Package: com.community.service
 * Description:
 *
 * @Author wth
 * @Create 2026/3/18 17:56
 * @Version 1.0
 */
public interface CategoryInfoService {
    List<CategoryVO> getCategories();
}
