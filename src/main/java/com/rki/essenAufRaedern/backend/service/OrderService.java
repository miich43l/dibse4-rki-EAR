package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.entity.Person;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the orders database table.
 */

@Service
public class OrderService {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private OrderService orderService;

    public OrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<Order> findAll() {
        return orderService.findAll();
    }


}