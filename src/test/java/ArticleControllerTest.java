
import com.community.controller.ArticleController;
import com.community.entity.pojo.Article;
import com.community.entity.pojo.ArticleDraft;
import com.community.entity.pojo.UserContext;
import com.community.entity.vo.PageResult;
import com.community.entity.vo.ResponseVO;
import com.community.service.ArticleInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ArticleController 单元测试
 */
@DisplayName("ArticleController 文章接口测试")
class ArticleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ArticleInfoService articleInfoService;

    @InjectMocks
    private ArticleController articleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(articleController).build();
    }

    @Test
    @DisplayName("发布文章 - 成功")
    void testPublishArticle_Success() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            doNothing().when(articleInfoService).publishArticle(
                    anyString(), anyInt(), anyString(), anyInt(), anyLong()
            );

            mockMvc.perform(get("/article/publish")
                            .param("title", "测试文章标题")
                            .param("categoryId", "1")
                            .param("content", "测试文章内容")
                            .param("contentType", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("文章发布成功"));

            verify(articleInfoService, times(1)).publishArticle(
                    "测试文章标题", 1, "测试文章内容", 1, 1L
            );
        }
    }

    @Test
    @DisplayName("删除文章 - 成功")
    void testDeleteArticle_Success() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            when(articleInfoService.deleteArticle(1L, 1L)).thenReturn(true);

            mockMvc.perform(get("/article/delete")
                            .param("articleId", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("删除成功"));

            verify(articleInfoService, times(1)).deleteArticle(1L, 1L);
        }
    }

    @Test
    @DisplayName("删除文章 - 失败")
    void testDeleteArticle_Fail() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            when(articleInfoService.deleteArticle(1L, 1L)).thenReturn(false);

            mockMvc.perform(get("/article/delete")
                            .param("articleId", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("删除失败"));

            verify(articleInfoService, times(1)).deleteArticle(1L, 1L);
        }
    }

    @Test
    @DisplayName("更新文章 - 成功")
    void testUpdateArticle_Success() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            doNothing().when(articleInfoService).updateArticle(
                    anyLong(), anyLong(), anyString(), anyInt(), anyString(), anyInt()
            );

            mockMvc.perform(get("/article/update")
                            .param("articleId", "1")
                            .param("title", "更新后的标题")
                            .param("categoryId", "2")
                            .param("content", "更新后的内容")
                            .param("contentType", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("文章更新成功"));

            verify(articleInfoService, times(1)).updateArticle(
                    1L, 1L, "更新后的标题", 2, "更新后的内容", 1
            );
        }
    }

    @Test
    @DisplayName("查询文章详情 - 成功")
    void testGetArticleDetail_Success() throws Exception {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("测试文章");

        when(articleInfoService.getArticleDetail(1L)).thenReturn(article);

        mockMvc.perform(get("/article/detail")
                        .param("articleId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("测试文章"));

        verify(articleInfoService, times(1)).getArticleDetail(1L);
    }

    @Test
    @DisplayName("保存草稿 - 成功")
    void testSaveDraft_Success() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            doNothing().when(articleInfoService).saveDraft(
                    anyString(), anyString(), anyInt(), anyLong()
            );

            mockMvc.perform(get("/article/draft/save")
                            .param("title", "草稿标题")
                            .param("content", "草稿内容")
                            .param("contentType", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("草稿保存成功"));

            verify(articleInfoService, times(1)).saveDraft(
                    "草稿标题", "草稿内容", 1, 1L
            );
        }
    }

    @Test
    @DisplayName("发布草稿 - 成功")
    void testPublishFromDraft_Success() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            doNothing().when(articleInfoService).publishFromDraft(
                    anyLong(), anyInt(), anyLong()
            );

            mockMvc.perform(get("/article/draft/publish")
                            .param("draftId", "1")
                            .param("categoryId", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("草稿发布成功"));

            verify(articleInfoService, times(1)).publishFromDraft(1L, 1, 1L);
        }
    }

    @Test
    @DisplayName("查询用户草稿 - 成功")
    void testGetUserDraft_Success() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            ArticleDraft draft = new ArticleDraft();
            draft.setId(1L);
            draft.setTitle("草稿标题");

            when(articleInfoService.getUserDraft(1L)).thenReturn(draft);

            mockMvc.perform(get("/article/draft/get")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("草稿标题"));

            verify(articleInfoService, times(1)).getUserDraft(1L);
        }
    }

    @Test
    @DisplayName("删除草稿 - 成功")
    void testDeleteDraft_Success() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            when(articleInfoService.deleteDraft(1L, 1L)).thenReturn(true);

            mockMvc.perform(get("/article/draft/delete")
                            .param("draftId", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("草稿删除成功"));

            verify(articleInfoService, times(1)).deleteDraft(1L, 1L);
        }
    }

    @Test
    @DisplayName("删除草稿 - 失败")
    void testDeleteDraft_Fail() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            when(articleInfoService.deleteDraft(1L, 1L)).thenReturn(false);

            mockMvc.perform(get("/article/draft/delete")
                            .param("draftId", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("草稿删除失败"));

            verify(articleInfoService, times(1)).deleteDraft(1L, 1L);
        }
    }

    @Test
    @DisplayName("更新草稿 - 成功")
    void testUpdateDraft_Success() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(1L);

            doNothing().when(articleInfoService).updateDraft(
                    anyLong(), anyLong(), anyString(), anyString(), anyInt()
            );

            mockMvc.perform(get("/article/draft/update")
                            .param("draftId", "1")
                            .param("title", "更新后的草稿标题")
                            .param("content", "更新后的草稿内容")
                            .param("contentType", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("草稿更新成功"));

            verify(articleInfoService, times(1)).updateDraft(
                    1L, 1L, "更新后的草稿标题", "更新后的草稿内容", 1
            );
        }
    }

    @Test
    @DisplayName("查询文章列表 - 成功")
    void testGetArticleList_Success() throws Exception {
        List<Article> articles = new ArrayList<>();
        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("文章1");
        articles.add(article1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("文章2");
        articles.add(article2);

        when(articleInfoService.getArticleList(1)).thenReturn(articles);

        mockMvc.perform(get("/article/list")
                        .param("categoryId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("文章1"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].title").value("文章2"));

        verify(articleInfoService, times(1)).getArticleList(1);
    }

    @Test
    @DisplayName("分页查询文章列表 - 成功")
    void testGetArticleListByPage_Success() throws Exception {
        PageResult<Article> pageResult = new PageResult<>();
        List<Article> articles = new ArrayList<>();
        Article article = new Article();
        article.setId(1L);
        article.setTitle("文章1");
        articles.add(article);

        pageResult.setList(articles);
        pageResult.setTotal(1L);
        pageResult.setPage(1);
        pageResult.setPageSize(10);

        when(articleInfoService.getArticleListByPage(1, 1, 10)).thenReturn(pageResult);

        mockMvc.perform(get("/article/list/page")
                        .param("categoryId", "1")
                        .param("page", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10));

        verify(articleInfoService, times(1)).getArticleListByPage(1, 1, 10);
    }
}
