package com.community.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}