package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.AddressRepository;
import com.rki.essenAufRaedern.backend.repository.PersonRepository;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the person database table.
 */
@Service
public class PersonService {
    private static final Logger LOGGER = Logger.getLogger(PersonService.class.getName());

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;

    public PersonService(PersonRepository personRepository, AddressRepository addressRepository) {
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findByFirstnameOrLastname(String name) {
        return personRepository.findByFirstNameContainingOrLastNameContaining(name, name);
    }
    public void delete(Person person) {
        person.setStatus(Status.Inactive);
        personRepository.save(person);
    }

    public void save(Person person) {
        if (person == null) {
            LOGGER.log(Level.SEVERE,
                    "Person is null");
            return;
        }
        personRepository.save(person);
    }
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public long count() {
        return personRepository.count();
    }

    public List<Person> getActiveClients() {
        return personRepository.findByPersonTypeAndStatus(PersonType.Client, Status.Active);
    }

    public List<Person> getClients() {
        return personRepository.findByPersonType(PersonType.Client);
    }


}