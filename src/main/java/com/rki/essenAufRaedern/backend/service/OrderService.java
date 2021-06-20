package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
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

    public long count() {
        return orderRepository.count();
    }

    public void delete(Order order) {
        orderRepository.delete(order);
    }

    public void save(Order order) {
        if (order == null) {
            LOGGER.log(Level.SEVERE,
                    "Order is null");
            return;
        }
        orderRepository.save(order);
    }
}