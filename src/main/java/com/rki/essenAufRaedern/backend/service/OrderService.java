package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.repository.OrderRepository;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * The service class for the orders database table.
 */

@Service
public class OrderService {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private final OrderRepository orderRepository;

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
        if (isNull(order)) return;
        order.setStatus(Status.INACTIVE);
        orderRepository.save(order);
    }

    public void save(Order order) {
        if (isNull(order)) return;
        orderRepository.save(order);
    }

    public List<Order> getOrdersForKitchenAndDay(Long kitchenId, Date date) {
        return orderRepository.findByDtAndKitchenIdAndStatus(date, kitchenId, Status.ACTIVE);
    }

    public void markAsDelivered(Order order) {
        order.setDelivered(new Timestamp(new Date().getTime()));
        orderRepository.save(order);
    }

    public void markAsNotDelivered(Order order) {
        order.setNotDeliverable(new Timestamp(new Date().getTime()));
        orderRepository.save(order);
    }

    private boolean isNull(Order order) {
        if (order == null) {
            LOGGER.log(Level.SEVERE,
                    "Order is null");
            return true;
        }
        return false;
    }
}