package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.AdditionalInformationRepository;
import com.rki.essenAufRaedern.backend.repository.AddressRepository;
import com.rki.essenAufRaedern.backend.repository.ContactPersonRepository;
import com.rki.essenAufRaedern.backend.repository.PersonRepository;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The service class for the person database table.
 */

@Service
public class PersonService {
    private static final Logger LOGGER = Logger.getLogger(PersonService.class.getName());

    private final PersonRepository personRepository;
    private final ContactPersonRepository contactPersonRepository;
    private final AddressRepository addressRepository;
    private final AdditionalInformationRepository additionalInformationRepository;

    public PersonService(PersonRepository personRepository, ContactPersonRepository contactPersonRepository, AddressRepository addressRepository, AdditionalInformationRepository additionalInformationRepository) {
        this.personRepository = personRepository;
        this.contactPersonRepository = contactPersonRepository;
        this.addressRepository = addressRepository;
        this.additionalInformationRepository = additionalInformationRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findByFirstnameOrLastname(String name) {
        return personRepository.findByFirstNameContainingOrLastNameContaining(name, name);
    }

    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public long count() {
        return personRepository.count();
    }

    public List<Person> getActiveClients() {
        return personRepository.findByPersonTypeAndStatus(PersonType.CLIENT, Status.ACTIVE);
    }

    public List<Person> getActiveClientsBySearchFieldInput(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return personRepository.findByPersonTypeAndStatus(PersonType.CLIENT, Status.ACTIVE);
        } else {
            return personRepository.findByFieldInputPersonTypeAndStatus(stringFilter, PersonType.CLIENT, Status.ACTIVE);
        }
    }

    public List<Person> getActiveClientsByName(String firstname, String lastname) {
        if (firstname == null && lastname == null){
            return  getActiveClients();
        }
        return personRepository.findDistinctByPersonTypeAndStatusAndFirstNameAndLastName(PersonType.CLIENT, Status.ACTIVE, firstname, lastname);
    }

    public void saveAllUnsavedAdditionalInformation(Person person) {
        person.getAdditionalInformation().forEach(item -> {
            if(item.getId() == null) {
                additionalInformationRepository.save(item);
            }
        });
    }

    public void saveAllUnsavedContactPersons(Person person) {
        person.getContactPersons().forEach(item -> {
            if(item.getId() == null) {
                if(item.getContactPersonFrom().getId() == null) {
                    personRepository.save(item.getContactPersonFrom());
                }
                contactPersonRepository.save(item);
            }
        });
    }

    public void delete(Person person) {
        if (isNull(person)) return;
        person.setStatus(Status.INACTIVE);
        if (null != person.getAddress()) {
            person.getAddress().setStatus(Status.INACTIVE);
            addressRepository.save(person.getAddress());
        }
        personRepository.save(person);
    }

    public void save(Person person) {
        if (isNull(person)) return;
        personRepository.save(person);
    }

    private boolean isNull(Person person) {
        if (person == null) {
            LOGGER.log(Level.SEVERE,
                    "Person is null");
            return true;
        }
        return false;
    }
}