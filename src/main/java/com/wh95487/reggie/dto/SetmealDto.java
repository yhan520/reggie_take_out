package com.wh95487.reggie.dto;

import com.wh95487.reggie.entity.Setmeal;
import com.wh95487.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;//套餐关联的菜品集合

    private String categoryName;//分类名称
}
