package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.ContactPerson;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the contact_person database table.
 */

@Service
public class ContactPersonService {
    private static final Logger LOGGER = Logger.getLogger(ContactPersonService.class.getName());
    private ContactPerson contactPerson;
}