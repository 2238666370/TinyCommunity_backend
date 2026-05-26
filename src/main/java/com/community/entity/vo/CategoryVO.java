package com.community.entity.vo;

import com.community.entity.pojo.CategoryInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName: CategoryVO
 * Package: com.community.entity.vo
 * Description:
 *
 * @Author wth
 * @Create 2026/3/18 17:57
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVO {
    CategoryInfo node;
    List<CategoryVO> children;
}
