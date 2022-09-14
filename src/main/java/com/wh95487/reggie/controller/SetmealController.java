package com.wh95487.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wh95487.reggie.common.R;
import com.wh95487.reggie.dto.SetmealDto;
import com.wh95487.reggie.entity.Category;
import com.wh95487.reggie.entity.Setmeal;
import com.wh95487.reggie.entity.SetmealDish;
import com.wh95487.reggie.service.CategoryService;
import com.wh95487.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存套餐
     * POST /setmeal
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * GET /setmeal/page
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page,int pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null, Setmeal::getName, name).orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, lqw);

        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> setmealPageRecords = setmealPage.getRecords();

        List<SetmealDto> setmealDtoList = setmealPageRecords.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }

            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * DELETE /setmeal?ids=1423640210125656065,1423338765002256385
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.deleteWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 修改套餐状态
     * POST /setmeal/status/0?ids=1569629118769057794
     */
    @PostMapping("/status/{status}")
    public R<String> stopSell(@PathVariable Integer status, @RequestParam List<Long> ids){
        log.info("ids:{}",ids);

        LambdaUpdateWrapper<Setmeal> luw = new LambdaUpdateWrapper<>();
        luw.set(Setmeal::getStatus, status).in(Setmeal::getId, ids);

        setmealService.update(luw);

        return R.success("套餐状态修改成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
