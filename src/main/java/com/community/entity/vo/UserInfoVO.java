package com.community.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别（0=未知/1=男/2=女，可根据业务定义枚举）
     */
    private Integer sex;

    /**
     * 昵称
     */
    private String userName;


    /**
     * 头像路径/URL
     */
    private String photo;

    private String accessToken;

    private String refreshToken;
}