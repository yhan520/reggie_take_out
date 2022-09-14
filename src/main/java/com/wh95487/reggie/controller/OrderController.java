package com.wh95487.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wh95487.reggie.common.BaseContext;
import com.wh95487.reggie.common.R;
import com.wh95487.reggie.entity.Orders;
import com.wh95487.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);

        orderService.submit(orders);

        return R.success("成功下单");
    }

    /**
     * 后端订单分页查询
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, Long number,
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime){
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(number != null, Orders::getNumber, number);
        lqw.between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime);

        orderService.page(ordersPage, lqw);

        return R.success(ordersPage);
    }

    /**
     * 用户订单分页查询
     * GET /order/userPage?page=1&pageSize=5
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(int page, int pageSize){
        Page<Orders> ordersPage = new Page<>();
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId, currentId).orderByDesc(Orders::getOrderTime);

        orderService.page(ordersPage, lqw);
        return R.success(ordersPage);
    }
}
