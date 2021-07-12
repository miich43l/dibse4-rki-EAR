package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.entity.ContactPerson;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.utility.ContactPersonType;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.TransactionScoped;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private AddressService addressService;

    @BeforeEach
    @TransactionScoped
    void setUp() {
        Person client = TestUtil.createDummyPerson();
        addressService.save(client.getAddress());
        personService.save(client);
    }
    @Test
    @TransactionScoped
    void findAll() {
        assertTrue(personService.findAll().stream()
                .anyMatch(p ->
                       p.getFirstName().equals("Max")
                                && p.getLastName().equals("Muster")));
    }

    @Test
    @TransactionScoped
    void findByFirstnameOrLastname() {
        createAdditionalData();
        assertTrue(personService.findByFirstnameOrLastname("Max").stream().findFirst().isPresent());
        assertTrue(personService.findByFirstnameOrLastname("Muster").stream().findFirst().isPresent());
    }

    @Test
    @TransactionScoped
    void findByFirstnameAndLastname() {
        assertTrue(personService.findByFirstnameAndLastname("Max", "Muster").stream().findFirst().isPresent());
    }

    @Test
    @TransactionScoped
    void findById() {
        Optional<Person> person = personService.findAll().stream()
                .filter(p -> p.getFirstName().equals("Max")
                        && p.getLastName().equals("Muster"))
                .findFirst();
        assertTrue(personService.findById(person.get().getId()).isPresent());
    }

    @Test
    @TransactionScoped
    void count() {
        Long countBefore = personService.count();
        createAdditionalData();
        Long countAfter = personService.count();
        assertEquals(countBefore, countAfter - 1);
    }

    @Test
    @TransactionScoped
    void getActiveClients() {
        Long countBefore = personService.getActiveClients().stream().count();
        Person client = TestUtil.createDummyPerson();
        addressService.save(client.getAddress());
        client.setPersonType(PersonType.DRIVER);
        personService.save(client);
        Long countAfter = personService.getActiveClients().stream().count();
        assertEquals(countBefore, countAfter);
        addressService.save(client.getAddress());
        client.setPersonType(PersonType.CLIENT);
        personService.save(client);
        countAfter = personService.getActiveClients().stream().count();
        assertEquals(countBefore, countAfter -1);
    }

    @Test
    @TransactionScoped
    void getActiveClientsBySearchFieldInput() {
        Optional<Person> person = personService.getActiveClientsBySearchFieldInput("Max").stream().findFirst();
        assertTrue(personService.findById(person.get().getId()).isPresent());
    }

    @Test
    @TransactionScoped
    void getActiveClientsByName() {
        Optional<Person> person = personService.getActiveClientsByName("Max", "Muster").stream().findFirst();
        assertTrue(personService.findById(person.get().getId()).isPresent());
    }

    @Test
    @TransactionScoped
    void saveAllUnsavedAdditionalInformation() {
        Person person = TestUtil.createDummyPerson();
        addressService.save(person.getAddress());
        personService.save(person);
        Set<AdditionalInformation> additionalInformationSet = new HashSet<>();
        AdditionalInformation additionalInformation = new AdditionalInformation();
        additionalInformation.setInformationType(InformationType.ADMINISTRATION);
        additionalInformation.setValue("Test");
        additionalInformation.setPerson(person);
        additionalInformation.setStatus(Status.ACTIVE);
        additionalInformationSet.add(additionalInformation);
        Long countAdditionalInformationBefore = person.getAdditionalInformation(InformationType.ADMINISTRATION).stream().count();
        person.setAdditionalInformation(additionalInformationSet);
        personService.saveAllUnsavedAdditionalInformation(person);
        Long countAdditionalInformationAfter =  person.getAdditionalInformation(InformationType.ADMINISTRATION).stream().count();
        assertEquals(countAdditionalInformationBefore, countAdditionalInformationAfter - 1);
    }

    @Test
    @TransactionScoped
    void saveAllUnsavedContactPersons() {
        Person person = TestUtil.createDummyPerson();
        addressService.save(person.getAddress());
        personService.save(person);
        Person cPerson = TestUtil.createDummyPerson();
        cPerson.setFirstName("Contact");
        cPerson.setLastName("Person");
        personService.save(cPerson);
        Set<ContactPerson> contactPeople = new HashSet<>();
        ContactPerson contactPerson = new ContactPerson();
        contactPerson.setPerson(cPerson);
        contactPerson.setContactPersonFrom(person);
        contactPerson.setContactPersonType(ContactPersonType.NEIGHBOR);
        contactPeople.add(contactPerson);
        Long countContactPersonsBefore = person.getContactPersons().stream().count();
        person.setContactPerson(contactPeople);
        personService.saveAllUnsavedContactPersons(person);
        Long countContactPersonsAfter =  person.getContactPersons().stream().count();
        assertEquals(countContactPersonsBefore, countContactPersonsAfter - 1);
    }

    @Test
    @TransactionScoped
    void delete() {
        Person person = TestUtil.createDummyPerson();
        addressService.save(person.getAddress());
        person.setPersonType(PersonType.CLIENT);
        personService.save(person);
        Long countBefore = personService.getActiveClients().stream().count();
        personService.delete(person);
        Long countAfter =  personService.getActiveClients().stream().count();
        assertEquals(countBefore, countAfter + 1);
    }

    @Test
    @TransactionScoped
    void save() {
        Long countBefore = personService.count();
        createAdditionalData();
        Long countAfter = personService.count();
        assertEquals(countBefore, countAfter - 1);
    }

    private void createAdditionalData() {
        Person client = TestUtil.createDummyPerson();
        client.setFirstName("Test2");
        client.setLastName("Client2");
        addressService.save(client.getAddress());
        personService.save(client);
    }
}