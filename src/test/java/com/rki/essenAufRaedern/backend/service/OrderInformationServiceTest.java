package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.OrderInformation;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.TransactionScoped;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrderInformationServiceTest {

    @Autowired
    OrderInformationService orderInformationService;

    @Autowired
    PersonService personService;

    @BeforeEach
    @TransactionScoped
    void setUp() {
        OrderInformation orderInformation = TestUtil.createDummyOrderInformation();
        personService.save(orderInformation.getPerson());
        orderInformationService.save(orderInformation);
    }

    @Test
    @TransactionScoped
    void findAll() {
        assertTrue(orderInformationService.findAll().stream()
                .anyMatch(o -> o.getPerson().getFirstName().equals("Max") && o.getPerson().getLastName().equals("Muster")));
    }

    @Test
    @TransactionScoped
    void count() {
        Long countBefore = orderInformationService.count();
        createAdditionalData();
        Long countAfter = orderInformationService.count();
        assertEquals(countBefore, countAfter - 1);
    }

    @Test
    @TransactionScoped
    void delete() {
        Optional<OrderInformation> orderInformation = orderInformationService.findAll().stream()
                .filter(o -> o.getPerson().getFirstName().equals("Max") && o.getPerson().getLastName().equals("Muster"))
                .findFirst();
        Status statusBefore = orderInformation.get().getStatus();
        assertNull(orderInformation.get().getDt_to());
        orderInformationService.delete(orderInformation.get());
        Status statusAfter = orderInformation.get().getStatus();
        assertNotEquals(statusBefore, statusAfter);
        assertNotNull(orderInformation.get().getDt_to());
    }

    @Test
    @TransactionScoped
    void save() {
        Long countBefore = orderInformationService.count();
        createAdditionalData();
        Long countAfter = orderInformationService.count();
        assertEquals(countBefore, countAfter - 1);
    }

    private void createAdditionalData() {
        OrderInformation orderInformation = TestUtil.createDummyOrderInformation();
        personService.save(orderInformation.getPerson());
        orderInformationService.save(orderInformation);
    }
}