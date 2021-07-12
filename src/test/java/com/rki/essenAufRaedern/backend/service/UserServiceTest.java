package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.User;
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
class UserServiceTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @BeforeEach
    @TransactionScoped
    void setUp() {
        User user = TestUtil.createDummyUser();
        addressService.save(user.getPerson().getAddress());
        personService.save(user.getPerson());
        userService.save(user);
    }

    @Test
    @TransactionScoped
    void findAll() {
        assertTrue(userService.findAll().stream()
                .anyMatch(u ->
                        u.getPerson().getFirstName().equals("Max")
                                && u.getPerson().getLastName().equals("Muster")));
    }

    @Test
    @TransactionScoped
    void count() {
        Long countBefore = userService.count();
        createAdditionalData();
        Long countAfter = userService.count();
        assertEquals(countBefore, countAfter - 1);
    }

    @Test
    @TransactionScoped
    void delete() {
        Optional<User> user = userService.findAll().stream()
                .filter(u -> u.getPerson().getFirstName().equals("Max")
                        && u.getPerson().getLastName().equals("Muster"))
                .findFirst();
        Status statusBefore = user.get().getStatus();
        userService.delete(user.get());
        Status statusAfter = user.get().getStatus();
        assertNotEquals(statusBefore, statusAfter);
    }

    @Test
    @TransactionScoped
    void save() {
        Long countBefore = userService.count();
        createAdditionalData();
        Long countAfter = userService.count();
        assertEquals(countBefore, countAfter - 1);
    }

    @Test
    @TransactionScoped
    void getUserByUsername() {
        assertTrue(userService.getUserByUsername("TestUser").getId() > 0);
    }

    @Test
    @TransactionScoped
    void getUserByUsernameIgnoreCase() {
        createAdditionalData();
        assertTrue(userService.getUserByUsernameIgnoreCase("TESTUSER2").getId() > 0);
    }

    private void createAdditionalData() {
        User user = TestUtil.createDummyUser();
        user.setUsername("TestUser2");
        addressService.save(user.getPerson().getAddress());
        personService.save(user.getPerson());
        userService.save(user);
    }
}