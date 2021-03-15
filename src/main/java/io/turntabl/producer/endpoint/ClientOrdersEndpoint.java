package io.turntabl.producer.endpoint;

import io.turntabl.producer.clientorders.OrderRequest;
import io.turntabl.producer.clientorders.OrderResponse;
import io.turntabl.producer.service.ClientOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ClientOrdersEndpoint {

    private static final String NAMESPACE = "http://www.turntabl.io/producer/clientOrders";

    @Autowired
    private ClientOrdersService service;

    @PayloadRoot(namespace = NAMESPACE, localPart = "OrderRequest")
    @ResponsePayload
    public OrderResponse getOrderValidility(@RequestPayload OrderRequest request){
        return service.checkOrderValidity(request);
    }
}
