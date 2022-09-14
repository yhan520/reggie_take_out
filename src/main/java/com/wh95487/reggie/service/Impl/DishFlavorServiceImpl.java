package com.wh95487.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wh95487.reggie.entity.DishFlavor;
import com.wh95487.reggie.mapper.DishFlavorMapper;
import com.wh95487.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
