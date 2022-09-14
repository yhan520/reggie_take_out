package com.wh95487.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wh95487.reggie.entity.User;
import com.wh95487.reggie.mapper.UserMapper;
import com.wh95487.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
