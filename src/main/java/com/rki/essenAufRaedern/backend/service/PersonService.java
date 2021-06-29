package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.OrderInformation;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.AddressRepository;
import com.rki.essenAufRaedern.backend.repository.PersonRepository;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public long count() {
        return personRepository.count();
    }

    public List<Person> getActiveClients() {
        return personRepository.findByPersonTypeAndStatus(PersonType.CLIENT, Status.ACTIVE);
    }

    public List<Person> getActiveClientsByName(String firstname, String lastname) {
        if (firstname == null && lastname == null){
            return  getActiveClients();
        }
        return personRepository.findDistinctByPersonTypeAndStatusAndFirstNameAndLastName(PersonType.CLIENT, Status.ACTIVE, firstname, lastname);
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

    public Person createNewPerson(PersonType personType) {
        Person newPerson = new Person();
        newPerson.setStatus(Status.ACTIVE);
        newPerson.setPersonType(personType);

        if(personType == PersonType.CLIENT) {
            Address address = new Address();
            address.setStatus(Status.ACTIVE);
            newPerson.setAddress(address);

            OrderInformation orderInformation = new OrderInformation();
            orderInformation.setStatus(Status.ACTIVE);
            orderInformation.setMonday(Status.INACTIVE);
            orderInformation.setTuesday(Status.INACTIVE);
            orderInformation.setWednesday(Status.INACTIVE);
            orderInformation.setThursday(Status.INACTIVE);
            orderInformation.setFriday(Status.INACTIVE);
            orderInformation.setSaturday(Status.INACTIVE);
            orderInformation.setSunday(Status.INACTIVE);
            orderInformation.setDt_form(new Date());

            newPerson.addOrderInformation(orderInformation);
        }

        return newPerson;
    }
}