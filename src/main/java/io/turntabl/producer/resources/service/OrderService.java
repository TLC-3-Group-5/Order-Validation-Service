package io.turntabl.producer.resources.service;

import io.turntabl.producer.resources.model.Orders;
import io.turntabl.producer.resources.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public void createOrders(Orders orders){
        this.orderRepository.save(orders);
    }
}
