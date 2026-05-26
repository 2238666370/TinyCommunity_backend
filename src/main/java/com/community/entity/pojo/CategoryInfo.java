package com.community.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.sql.In;

/**
 * ClassName: CategoryInfo
 * Package: com.community.entity.pojo
 * Description:
 *
 * @Author wth
 * @Create 2026/3/18 17:53
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryInfo {
    Integer id;
    String name;
    Integer parentId;
}
