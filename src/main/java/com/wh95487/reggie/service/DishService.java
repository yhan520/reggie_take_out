package com.wh95487.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wh95487.reggie.dto.DishDto;
import com.wh95487.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);
    DishDto getByIdWithFlavor(Long id);
    void updateWithFlavor(DishDto dishDto);
}
