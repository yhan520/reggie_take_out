package com.wh95487.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wh95487.reggie.common.CustomException;
import com.wh95487.reggie.entity.Category;
import com.wh95487.reggie.entity.Dish;
import com.wh95487.reggie.entity.Setmeal;
import com.wh95487.reggie.mapper.CategoryMapper;
import com.wh95487.reggie.service.CategoryService;
import com.wh95487.reggie.service.DishService;
import com.wh95487.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long ids) {

        LambdaQueryWrapper<Dish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Dish::getCategoryId, ids);
        int dishCount = dishService.count(lqw1);
        if(dishCount > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(Setmeal::getCategoryId, ids);
        int setmealCount = setmealService.count(lqw2);
        if(setmealCount > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        super.removeById(ids);
    }
}
