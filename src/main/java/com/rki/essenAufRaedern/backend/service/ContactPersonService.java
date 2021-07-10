package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.ContactPerson;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.ContactPersonRepository;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The service class for the contact_person database table.
 */

@Service
public class ContactPersonService {
    private static final Logger LOGGER = Logger.getLogger(ContactPersonService.class.getName());

    private final ContactPersonRepository contactPersonRepository;

    public ContactPersonService(ContactPersonRepository contactPersonRepository) {
        this.contactPersonRepository = contactPersonRepository;
    }

    public void save(ContactPerson contactPerson) {
        if (contactPerson == null) {
            LOGGER.log(Level.SEVERE,
                    "ContactPerson is null");
            return;
        }

        contactPersonRepository.save(contactPerson);
    }

    public void delete(ContactPerson contactPerson) {
        if (contactPerson == null) {
            return;
        }

        contactPersonRepository.delete(contactPerson);
    }
}