package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.repository.AddressRepository;
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
class AddressServiceTest {

    @Autowired
    AddressService service;

    @Autowired
    AddressRepository addressRepository;

    @Test
    @TransactionScoped
    void save() {
        int sizeBefore = service.findAll().size();

        Address address = TestUtil.createDummyAddress();
        service.save(address);

        int sizeAfter = service.findAll().size();
        assertEquals(sizeBefore + 1, sizeAfter);
    }

    @Test
    @TransactionScoped
    void delete() {
        Address address = TestUtil.createDummyAddress();
        service.save(address);
        service.delete(address);

        Optional<Address> deletedAddress = addressRepository.findById(address.getId());
        assertFalse(deletedAddress.isEmpty());

        assertEquals(Status.INACTIVE, deletedAddress.get().getStatus());
    }

    @Test
    void findAll() {
        Address address = TestUtil.createDummyAddress();
        service.save(address);

        assertNotEquals(0, service.findAll().size());
    }
}