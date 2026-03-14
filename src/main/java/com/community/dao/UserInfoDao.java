package com.community.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.dao.mapper.UserInfoMapper;
import com.community.entity.pojo.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户信息数据访问层
 * 封装底层数据操作细节，为Service层提供简洁接口
 */
@Repository
public class UserInfoDao extends ServiceImpl<UserInfoMapper, UserInfo> {
    public UserInfo selectByEmail(String email) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getEmail, email);
        return this.getOne(queryWrapper);
    }

    public UserInfo selectByUserName(String userName) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserName, userName);
        return this.getOne(queryWrapper);
    }
}