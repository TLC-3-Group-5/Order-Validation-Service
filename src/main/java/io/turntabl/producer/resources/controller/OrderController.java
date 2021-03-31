package io.turntabl.producer.resources.controller;

import io.turntabl.producer.resources.model.Trade;
import io.turntabl.producer.resources.model.TradeList;
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
    public TradeList getOrderTrade(@PathVariable("orderId") String orderId){
        List<Trade> trades = orderService.getOrderTrades(orderId);
        TradeList tradeList = new TradeList();
        tradeList.setTradeList(trades);
        return tradeList;
    }

    @PutMapping(path="update-order-status/{orderId}")
    public void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody String status){
        orderService.updateOrderStatus(orderId,status);
    }

    @PutMapping(path="update-balance-of-cancelled-order/{orderId}")
    public void updateBalanceOfCancelledOrder(@PathVariable("orderId") String orderId){
        orderService.updateBalanceOfCancelledOrder(orderId);
    }
}
