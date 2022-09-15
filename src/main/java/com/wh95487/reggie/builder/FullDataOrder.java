package com.wh95487.reggie.builder;

import com.wh95487.reggie.entity.OrderDetail;
import com.wh95487.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class FullDataOrder {
    private Orders orders;
    private List<OrderDetail> orderDetailList;
}
