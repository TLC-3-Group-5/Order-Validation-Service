package io.turntabl.producer.resources.controller;

import io.turntabl.producer.resources.model.Trade;
import io.turntabl.producer.resources.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(path="order-trades/{orderId}")
    public List<Trade> getOrderTrade(@PathVariable("orderId") String orderId){
        return orderService.getOrderTrades(orderId);
    }

    @PutMapping(path="update-order-status/{orderId}")
    public void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody String status){
        orderService.updateOrderStatus(orderId,status);
    }
}
