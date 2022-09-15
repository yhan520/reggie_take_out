package com.wh95487.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wh95487.reggie.common.R;
import com.wh95487.reggie.dto.DishDto;
import com.wh95487.reggie.entity.Category;
import com.wh95487.reggie.entity.Dish;
import com.wh95487.reggie.entity.DishFlavor;
import com.wh95487.reggie.entity.Orders;
import com.wh95487.reggie.service.CategoryService;
import com.wh95487.reggie.service.DishFlavorService;
import com.wh95487.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("成功添加新菜品");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page (int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null, Dish::getName, name);
        lqw.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, lqw);

        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = records.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

    /**
     * 用处：1.新增套餐时，根据分类，查询当前分类下的菜品  GET /dish/list?categoryId=1397844263642378242&status=1
     *      2.
     * */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = null;
        //动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先在缓存中查找，如果能找到就直接返回
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishDtoList != null){
            return R.success(dishDtoList);
        }

        //无法在缓存中取到，就去数据库中查找
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        lqw.eq(Dish::getStatus, 1);
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(lqw); //到这一步就可以实现用处1的功能

        dishDtoList = dishList.stream().map(dish1 -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);

            Long categoryId = dish1.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            Long dish1Id = dish1.getId();
            LambdaQueryWrapper<DishFlavor> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(DishFlavor::getDishId, dish1Id);
            List<DishFlavor> dishFlavors = dishFlavorService.list(lqw1);
            dishDto.setFlavors(dishFlavors);

            return dishDto;

        }).collect(Collectors.toList());
        //将从数据库中查到的数据放入缓存中，并设置过期时间
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

    /**
     * 修改餐品状态
     * POST /dish/status/1?ids=1567507772853391361,1413384757047271425
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,  @RequestParam List<Long> ids){
        LambdaUpdateWrapper<Dish> luw = new LambdaUpdateWrapper<>();
        luw.set(Dish::getStatus, status).in(Dish::getId, ids);
        dishService.update(luw);
        //redisTemplate.delete()

        return R.success("修改成功");
    }

    /**
     * 删除菜品
     * DELETE /dish?ids=1567507772853391361,1413384757047271425,1413385247889891330
     */
    @DeleteMapping
    public R<String> remove(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids);
        dishService.remove(lqw);
        //redisTemplate.delete()

        return R.success("删除成功");
    }
}
