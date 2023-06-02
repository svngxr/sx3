package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    Orders selectDetailsById(int ordersId);

    List<Orders> selectDetails();

    List<Orders> getOrdersByPaymentMethod(String paymentMethod);

    List<Orders> getOrdersByStatus(String ordersStatus);

    List<Orders> getOrdersByPaymentMethodAndStatus(String paymentMethod, String ordersStatus);
}
