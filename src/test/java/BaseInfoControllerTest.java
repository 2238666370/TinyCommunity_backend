
import com.community.controller.BaseInfoController;
import com.community.entity.pojo.CategoryInfo;
import com.community.entity.vo.CategoryVO;
import com.community.entity.vo.ResponseVO;
import com.community.service.CategoryInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BaseInfoController 单元测试
 */
@DisplayName("BaseInfoController 基础信息接口测试")
class BaseInfoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryInfoService categoryInfoService;

    @InjectMocks
    private BaseInfoController baseInfoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(baseInfoController).build();
    }

    @Test
    @DisplayName("获取分类列表 - 成功")
    void testGetCategories_Success() throws Exception {
        List<CategoryVO> categories = new ArrayList<>();

        CategoryInfo categoryInfo1 = new CategoryInfo();
        categoryInfo1.setId(1);
        categoryInfo1.setName("技术");
        CategoryVO category1 = new CategoryVO(categoryInfo1, new ArrayList<>());
        categories.add(category1);

        CategoryInfo categoryInfo2 = new CategoryInfo();
        categoryInfo2.setId(2);
        categoryInfo2.setName("生活");
        CategoryVO category2 = new CategoryVO(categoryInfo2, new ArrayList<>());
        categories.add(category2);

        when(categoryInfoService.getCategories()).thenReturn(categories);

        mockMvc.perform(get("/baseInfo/getCategories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].node.categoryId").value(1))
                .andExpect(jsonPath("$.data[0].node.categoryName").value("技术"))
                .andExpect(jsonPath("$.data[1].node.categoryId").value(2))
                .andExpect(jsonPath("$.data[1].node.categoryName").value("生活"));

        verify(categoryInfoService, times(1)).getCategories();
    }

    @Test
    @DisplayName("获取分类列表 - 空列表")
    void testGetCategories_EmptyList() throws Exception {
        List<CategoryVO> categories = new ArrayList<>();
        when(categoryInfoService.getCategories()).thenReturn(categories);

        mockMvc.perform(get("/baseInfo/getCategories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(categoryInfoService, times(1)).getCategories();
    }
}
