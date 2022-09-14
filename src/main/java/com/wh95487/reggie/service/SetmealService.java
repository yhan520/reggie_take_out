package com.wh95487.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wh95487.reggie.dto.SetmealDto;
import com.wh95487.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void deleteWithDish(List<Long> ids);
}
