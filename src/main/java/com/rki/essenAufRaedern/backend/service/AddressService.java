package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.AddressRepository;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the address database table.
 */

@Service
public class AddressService {
    private static final Logger LOGGER = Logger.getLogger(AddressService.class.getName());
    private AddressRepository addressRepository;


    public void save(Address address) {
        if (address == null) {
            LOGGER.log(Level.SEVERE,
                    "Address is null");
            return;
        }
        addressRepository.save(address);
    }
    @PostConstruct

}
