
import com.community.controller.AccountController;
import com.community.entity.pojo.CaptchaResult;
import com.community.entity.vo.CheckCodeVO;
import com.community.entity.vo.ResponseVO;
import com.community.entity.vo.UserInfoVO;
import com.community.enums.ResponseCodeEnum;
import com.community.exception.BusinessException;
import com.community.service.RedisService;
import com.community.service.UserInfoService;
import com.community.util.CaptchaGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AccountController 单元测试
 */
@DisplayName("AccountController 用户账户接口测试")
class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserInfoService userInfoService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    @DisplayName("获取验证码 - 成功")
    void testCheckCode_Success() throws Exception {
        // 注意：由于CaptchaGenerator是静态工具类，实际测试中可能需要PowerMock
        // 这里测试Controller逻辑部分

        mockMvc.perform(get("/user/checkCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("登录 - 成功")
    void testLogin_Success() throws Exception {
        String key = "test-key-123";
        String email = "test@example.com";
        String password = "password123";
        String code = "123456";

        when(redisService.getCheckCode(key)).thenReturn(code);

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId(1L);
        userInfoVO.setEmail(email);
        userInfoVO.setUserName("testuser");
        when(userInfoService.login(email, password)).thenReturn(userInfoVO);

        mockMvc.perform(get("/user/login")
                        .param("key", key)
                        .param("email", email)
                        .param("password", password)
                        .param("code", code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.email").value(email));

        verify(redisService, times(1)).getCheckCode(key);
        verify(userInfoService, times(1)).login(email, password);
    }

    @Test
    @DisplayName("登录 - 验证码错误")
    void testLogin_WrongCode() {
        String key = "test-key-123";
        String email = "test@example.com";
        String password = "password123";
        String code = "123456";

        when(redisService.getCheckCode(key)).thenReturn("654321");

        assertThrows(BusinessException.class, () -> {
            accountController.login(key, email, password, code);
        });

        verify(redisService, times(1)).getCheckCode(key);
        verify(userInfoService, never()).login(anyString(), anyString());
    }

    @Test
    @DisplayName("注册 - 成功")
    void testRegister_Success() throws Exception {
        String key = "test-key-123";
        String email = "test@example.com";
        String password = "password123";
        String userName = "testuser";
        String code = "123456";

        when(redisService.getCheckCode(key)).thenReturn(code);
        when(userInfoService.register(email, password, userName)).thenReturn(true);

        mockMvc.perform(get("/user/register")
                        .param("key", key)
                        .param("email", email)
                        .param("password", password)
                        .param("userName", userName)
                        .param("code", code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        verify(redisService, times(1)).getCheckCode(key);
        verify(userInfoService, times(1)).register(email, password, userName);
    }

    @Test
    @DisplayName("注册 - 验证码错误")
    void testRegister_WrongCode() {
        String key = "test-key-123";
        String email = "test@example.com";
        String password = "password123";
        String userName = "testuser";
        String code = "123456";

        when(redisService.getCheckCode(key)).thenReturn("654321");

        assertThrows(BusinessException.class, () -> {
            accountController.register(key, email, password, userName, code);
        });

        verify(redisService, times(1)).getCheckCode(key);
        verify(userInfoService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("登出 - 成功")
    void testLogout_Success() throws Exception {
        String refreshToken = "refresh-token-123";

        doNothing().when(redisService).deleteRefreshToken(refreshToken);

        mockMvc.perform(get("/user/logout")
                        .param("refreshToken", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.code").value(200));

        verify(redisService, times(1)).deleteRefreshToken(refreshToken);
    }
}
