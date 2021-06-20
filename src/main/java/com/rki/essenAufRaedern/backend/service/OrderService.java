package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.OrderRepository;
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
    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }


}