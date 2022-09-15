package com.wh95487.reggie.builder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wh95487.reggie.common.BaseContext;
import com.wh95487.reggie.common.CustomException;
import com.wh95487.reggie.entity.*;
import com.wh95487.reggie.service.AddressBookService;
import com.wh95487.reggie.service.ShoppingCartService;
import com.wh95487.reggie.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NewFullOrderBuilder {

    private FullDataOrder fullDataOrder; //要build的对象
    private Orders orders;
    private AtomicInteger amount = new AtomicInteger(0); //订单总金额

    private ShoppingCartService shoppingCartService;
    private AddressBookService addressBookService;
    private UserService userService;

    public NewFullOrderBuilder(Orders orders, ShoppingCartService shoppingCartService, AddressBookService addressBookService, UserService userService){
        this.orders = orders;
        this.shoppingCartService = shoppingCartService;
        this.addressBookService = addressBookService;
        this.userService = userService;
        fullDataOrder = new FullDataOrder();
    }

    public NewFullOrderBuilder buildOrderDetail(){
        Long userId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lqw);

        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orders.getId());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setImage(item.getImage());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setName(item.getName());
            orderDetail.setAmount(item.getAmount());

            BigDecimal multiply = item.getAmount().multiply(new BigDecimal(item.getNumber())); //商品的单价×数量，计算出此种商品的总价格
            amount.addAndGet(multiply.intValue()); //购物车中所有商品的总价格
            return orderDetail;
        }).collect(Collectors.toList());
        //System.out.println(orderDetailList);
        fullDataOrder.setOrderDetailList(orderDetailList);
        return this;
    }

    public NewFullOrderBuilder buildOrder(){
        Long orderId = orders.getId(); //获取订单Id
        Long userId = BaseContext.getCurrentId(); //获取当前用户Id

        //获取订单传过来的地址的Id,并在数据库中进行查询，查看是否有此地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        User user = userService.getById(userId); //查询用户的基本信息

        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setOrderTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setPhone(user.getPhone());

        //地址相关的设置
        orders.setAddressBookId(addressBookId);
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orders.setConsignee(addressBook.getConsignee());

        fullDataOrder.setOrders(orders);
        return this;
    }

    public FullDataOrder build(){
        return fullDataOrder;
    }


}
