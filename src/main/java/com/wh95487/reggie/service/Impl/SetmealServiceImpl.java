package com.wh95487.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wh95487.reggie.common.CustomException;
import com.wh95487.reggie.dto.SetmealDto;
import com.wh95487.reggie.entity.Setmeal;
import com.wh95487.reggie.entity.SetmealDish;
import com.wh95487.reggie.mapper.SetmealMapper;
import com.wh95487.reggie.service.SetmealDishService;
import com.wh95487.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        System.out.println(setmealDto.getId());
        //先保存套餐
        this.save(setmealDto);

        System.out.println(setmealDto.getId());

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().peek(item -> item.setSetmealId(setmealDto.getId())).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids).eq(Setmeal::getStatus, 1);
        int count = this.count(lqw);
        if(count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(lqw1);
    }
}
