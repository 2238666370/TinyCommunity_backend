package com.community.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: ArticleContent
 * Package: com.community.entity.pojo
 * Description:
 *
 * @Author wth
 * @Create 2026/3/18 19:44
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleContent {
    Long id;
    Long articleId;
    Integer contentType;
    String rawContent;
    String htmlContent;
}
