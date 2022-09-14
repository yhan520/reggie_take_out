package com.wh95487.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wh95487.reggie.entity.OrderDetail;
import com.wh95487.reggie.mapper.OrderDetailMapper;
import com.wh95487.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
