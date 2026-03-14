package com.community.service;

import com.community.entity.vo.UserInfoVO;

/**
 * ClassName: UserInfoService
 * Package: com.community.service
 * Description:
 *
 * @Author wth
 * @Create 2026/3/10 23:59
 * @Version 1.0
 */
public interface UserInfoService {
    UserInfoVO login(String email, String password);

    Boolean register(String email, String password, String userName);
}
