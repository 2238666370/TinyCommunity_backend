package com.community.controller;

import com.community.annotation.GlobalInterceptor;
import com.community.entity.pojo.Article;
import com.community.entity.pojo.ArticleDraft;
import com.community.entity.pojo.UserContext;
import com.community.entity.vo.ResponseVO;
import com.community.service.ArticleInfoService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: ArticleController
 * Package: com.community.controller
 * Description: 文章控制器
 *
 * @Author wth
 * @Create 2026/3/15 17:12
 * @Version 1.0
 */
@RestController
@RequestMapping("/article")
@Slf4j
public class ArticleController extends ABaseController {
    
    @Resource
    ArticleInfoService articleInfoService;

    /**
     * 发布文章
     */
    @RequestMapping("/publish")
    @GlobalInterceptor
    public ResponseVO publishArticle(@NotEmpty String title,
                                    @NotNull Integer categoryId,
                                    @NotEmpty String content,
                                    @NotNull Integer contentType) {
        Long userId = UserContext.getUserId();
        articleInfoService.publishArticle(title, categoryId, content, contentType, userId);
        return this.getSuccessResponseVO("文章发布成功");
    }

    /**
     * 删除文章
     */
    @RequestMapping("/delete")
    @GlobalInterceptor
    public ResponseVO deleteArticle(@NotNull Long articleId) {
        Long userId = UserContext.getUserId();
        boolean res = articleInfoService.deleteArticle(articleId, userId);
        return this.getSuccessResponseVO(res ? "删除成功" : "删除失败");
    }

    /**
     * 更新文章
     */
    @RequestMapping("/update")
    @GlobalInterceptor
    public ResponseVO updateArticle(@NotNull Long articleId,
                                   String title,
                                   Integer categoryId,
                                   String content,
                                   Integer contentType) {
        Long userId = UserContext.getUserId();
        articleInfoService.updateArticle(articleId, userId, title, categoryId, content, contentType);
        return this.getSuccessResponseVO("文章更新成功");
    }

    /**
     * 查询文章详情
     */
    @RequestMapping("/detail")
    @GlobalInterceptor
    public ResponseVO getArticleDetail(@NotNull Long articleId) {
        Article article = articleInfoService.getArticleDetail(articleId);
        return this.getSuccessResponseVO(article);
    }

    /**
     * 保存草稿
     */
    @RequestMapping("/draft/save")
    @GlobalInterceptor
    public ResponseVO saveDraft(String title,
                               String content,
                               Integer contentType) {
        Long userId = UserContext.getUserId();
        articleInfoService.saveDraft(title, content, contentType, userId);
        return this.getSuccessResponseVO("草稿保存成功");
    }

    /**
     * 发布草稿
     */
    @RequestMapping("/draft/publish")
    @GlobalInterceptor
    public ResponseVO publishFromDraft(@NotNull Long draftId,
                                      @NotNull Integer categoryId) {
        Long userId = UserContext.getUserId();
        articleInfoService.publishFromDraft(draftId, categoryId, userId);
        return this.getSuccessResponseVO("草稿发布成功");
    }

    /**
     * 查询用户草稿
     */
    @RequestMapping("/draft/get")
    @GlobalInterceptor
    public ResponseVO getUserDraft() {
        Long userId = UserContext.getUserId();
        ArticleDraft draft = articleInfoService.getUserDraft(userId);
        return this.getSuccessResponseVO(draft);
    }

    /**
     * 删除草稿
     */
    @RequestMapping("/draft/delete")
    @GlobalInterceptor
    public ResponseVO deleteDraft(@NotNull Long draftId) {
        Long userId = UserContext.getUserId();
        boolean res = articleInfoService.deleteDraft(draftId, userId);
        return this.getSuccessResponseVO(res ? "草稿删除成功" : "草稿删除失败");
    }

    /**
     * 更新草稿
     */
    @RequestMapping("/draft/update")
    @GlobalInterceptor
    public ResponseVO updateDraft(@NotNull Long draftId,
                                 String title,
                                 String content,
                                 Integer contentType) {
        Long userId = UserContext.getUserId();
        articleInfoService.updateDraft(draftId, userId, title, content, contentType);
        return this.getSuccessResponseVO("草稿更新成功");
    }
}
