package com.wh95487.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wh95487.reggie.builder.FullDataOrder;
import com.wh95487.reggie.builder.NewFullOrderBuilder;
import com.wh95487.reggie.common.BaseContext;
import com.wh95487.reggie.common.CustomException;
import com.wh95487.reggie.entity.*;
import com.wh95487.reggie.mapper.OrderMapper;
import com.wh95487.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    public void submit(Orders orders) {
        orders.setId(IdWorker.getId());
        NewFullOrderBuilder newFullOrderBuilder = new NewFullOrderBuilder(orders,shoppingCartService,addressBookService,userService);
        FullDataOrder fullDataOrder = newFullOrderBuilder.buildOrderDetail().buildOrder().build();

        //向订单表插入数据，一条数据
        this.save(fullDataOrder.getOrders());
        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(fullDataOrder.getOrderDetailList());

        //清空购物车数据
        shoppingCartService.remove(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId,BaseContext.getCurrentId()));
    }
}
