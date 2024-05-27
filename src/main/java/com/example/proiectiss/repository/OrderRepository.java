package com.example.proiectiss.repository;

import com.example.proiectiss.domain.Order;
import com.example.proiectiss.domain.OrderItem;

import java.util.List;

public interface OrderRepository extends IRepository<Order>{
    List<Order> getAllOrdersForSection(String username);
    void honourOrder(Order order);
    void saveOrder(Order order, List<OrderItem> orderItems);
    List<OrderItem> getAllOrderItemsForOrder(Integer orderId);
}
