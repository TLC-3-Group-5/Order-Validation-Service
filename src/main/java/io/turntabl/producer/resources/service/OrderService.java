package io.turntabl.producer.resources.service;

import io.turntabl.producer.resources.model.Orders;
import io.turntabl.producer.resources.model.Trade;
import io.turntabl.producer.resources.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    // Create Order
    public void createOrders(Orders orders){
        this.orderRepository.save(orders);
    }

    // Get Order
    public List<Trade> getOrderTrades(String id){
        Orders order = orderRepository.findById(Long.valueOf(id)).orElse(null);
        return order != null ?
                order.getTradeList()
                        .stream()
                        .filter(s->s.getStatus().equals("OPEN")).collect(Collectors.toList())
                : null;
    }

    // Update Order Status
    public void updateOrderStatus(Long id, String status){
        Orders order = orderRepository.findById(id).orElse(null);
        if(order!=null){
            order.setStatus(status);
            orderRepository.save(order);
        }
    }
}
