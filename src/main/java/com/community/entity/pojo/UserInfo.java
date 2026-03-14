package com.community.entity.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户信息表对应实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    /**
     * 用户ID（主键）
     */
    private Long userId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码（MD5加密）
     */
    private String password;

    /**
     * 盐值
     */
    private Integer salt;

    /**
     * 性别：0-男，1-女，2-未知
     */
    private Integer sex;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    /**
     * 创建时间（时间戳，单位毫秒）
     */
    private Long createTime;

    /**
     * 上次登录时间（时间戳，单位毫秒）
     */
    private Long lastLoginTime;

    /**
     * 头像图片地址
     */
    private String photo;

    /**
     * 角色：2-普通用户，1-VIP用户，0-管理员
     */
    private Integer role;
}