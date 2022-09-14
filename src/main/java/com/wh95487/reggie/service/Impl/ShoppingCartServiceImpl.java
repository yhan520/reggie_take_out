package com.wh95487.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wh95487.reggie.entity.ShoppingCart;
import com.wh95487.reggie.mapper.ShoppingCartMapper;
import com.wh95487.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
