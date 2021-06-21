package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.AddressRepository;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author arthurwaldner
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
        if (address == null) {
            LOGGER.log(Level.SEVERE,
                    "Address is null");
            return;
        }
        addressRepository.save(address);
    }

    public void delete(Address address) {
        if(address == null) {
            return;
        }

        addressRepository.delete(address);
    }

    public List<Address> findAll() {
        return addressRepository.findAll();
    }
}
