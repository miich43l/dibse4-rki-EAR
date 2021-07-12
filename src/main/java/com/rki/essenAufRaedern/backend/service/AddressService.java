package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.repository.AddressRepository;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The service class for the address database table.
 */

@Service
public class AddressService {
    private static final Logger LOGGER = Logger.getLogger(AddressService.class.getName());
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void save(Address address) {
        if (isNull(address)) return;
        addressRepository.save(address);
    }

    public void delete(Address address) {
        if (isNull(address)) return;
        address.setStatus(Status.INACTIVE);
        addressRepository.save(address);
    }

    private boolean isNull(Address address) {
        if (address == null) {
            LOGGER.log(Level.SEVERE,
                    "Address is null");
            return true;
        }
        return false;
    }

    public List<Address> findAll() {
        return addressRepository.findAll();
    }
}
