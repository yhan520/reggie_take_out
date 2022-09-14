package com.wh95487.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wh95487.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
