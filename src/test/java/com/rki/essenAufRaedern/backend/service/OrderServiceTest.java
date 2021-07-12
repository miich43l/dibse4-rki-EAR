package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.TransactionScoped;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private KitchenService kitchenService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    @TransactionScoped
    void setUp() {
        Order order = TestUtil.createDummyOrder();
        addressService.save(order.getKitchen().getAddress());
        personService.save(order.getPerson());
        kitchenService.save(order.getKitchen());
        orderService.save(order);
    }

    @Test
    @TransactionScoped
    void findAll() {
        assertTrue(orderService.findAll().stream()
                .anyMatch(o ->
                        o.getPerson().getFirstName().equals("Max")
                                && o.getPerson().getLastName().equals("Muster")
                                && o.getKitchen().getName().equals("DUMMY")));
    }

    @Test
    @TransactionScoped
    void count() {
        Long countBefore = orderService.count();
        createAdditionalData();
        Long countAfter = orderService.count();
        assertEquals(countBefore, countAfter - 1);
    }

    @Test
    @TransactionScoped
    void delete() {
        Optional<Order> order = orderService.findAll().stream()
                .filter(o -> o.getPerson().getFirstName().equals("Max")
                        && o.getPerson().getLastName().equals("Muster")
                        && o.getKitchen().getName().equals("DUMMY"))
                .findFirst();
        Status statusBefore = order.get().getStatus();
        orderService.delete(order.get());
        Status statusAfter = order.get().getStatus();
        assertNotEquals(statusBefore, statusAfter);
    }

    @Test
    @TransactionScoped
    void save() {
        Long countBefore = orderService.count();
        createAdditionalData();
        Long countAfter = orderService.count();
        assertEquals(countBefore, countAfter - 1);
    }

    @Test
    @TransactionScoped
    void getOrdersForKitchenAndDay() {
        createAdditionalData();
        Long kitchenId = orderService.findAll().stream()
                .filter(o -> o.getPerson().getFirstName().equals("Test2")
                        && o.getPerson().getLastName().equals("Client")
                        && o.getKitchen().getName().equals("DUMMY"))
                .findFirst().get()
                .getKitchen()
                .getId();
        assertFalse(orderService.getOrdersForKitchenAndDay(kitchenId, new Date()).isEmpty());
    }

    @Test
    @TransactionScoped
    void markAsDelivered() {
        Optional<Order> order = orderService.findAll().stream()
                .filter(o -> o.getPerson().getFirstName().equals("Max")
                        && o.getPerson().getLastName().equals("Muster")
                        && o.getKitchen().getName().equals("DUMMY"))
                .findFirst();
        Timestamp deliveredBefore = order.get().getDelivered();
        orderService.markAsDelivered(order.get());
        Timestamp deliveredAfter = order.get().getDelivered();
        assertNotEquals(deliveredBefore, deliveredAfter);
    }

    @Test
    @TransactionScoped
    void markAsNotDelivered() {
        Optional<Order> order = orderService.findAll().stream()
                .filter(o -> o.getPerson().getFirstName().equals("Max")
                        && o.getPerson().getLastName().equals("Muster")
                        && o.getKitchen().getName().equals("DUMMY"))
                .findFirst();
        Timestamp notDeliverableBefore = order.get().getNotDeliverable();
        orderService.markAsNotDelivered(order.get());
        Timestamp notDeliverableAfter = order.get().getNotDeliverable();
        assertNotEquals(notDeliverableBefore, notDeliverableAfter);
    }

    private void createAdditionalData() {
        Order order = TestUtil.createDummyOrder();
        addressService.save(order.getKitchen().getAddress());
        order.getPerson().setFirstName("Test2");
        order.getPerson().setLastName("Client");
        personService.save(order.getPerson());
        kitchenService.save(order.getKitchen());
        orderService.save(order);
    }
}