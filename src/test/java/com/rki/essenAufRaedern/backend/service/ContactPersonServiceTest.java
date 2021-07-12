package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.ContactPerson;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.ContactPersonRepository;
import com.rki.essenAufRaedern.backend.repository.PersonRepository;
import com.rki.essenAufRaedern.backend.utility.ContactPersonType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.TransactionScoped;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContactPersonServiceTest {

    @Autowired
    ContactPersonService service;

    @Autowired
    ContactPersonRepository contactPersonRepository;

    @Autowired
    PersonRepository personRepository;

    private ContactPerson insertDummyContactPersonThatIsHisHerOwnContactPerson() {
        Person person = TestUtil.createDummyPerson();
        personRepository.save(person);

        ContactPerson contactPerson = new ContactPerson();
        contactPerson.setContactPersonType(ContactPersonType.NEIGHBOR);
        contactPerson.setPerson(person);
        contactPerson.setContactPersonFrom(person);

        service.save(contactPerson);

        return contactPerson;
    }

    @Test
    void getContactPersons() {
        ContactPerson contactPerson = insertDummyContactPersonThatIsHisHerOwnContactPerson();

        List<ContactPerson> contactPersonList = service.getContactPersons(contactPerson.getPerson().getId());
        assertNotEquals(0, contactPersonList.size());

        assertEquals(contactPersonList.get(0).getPerson().getId(), contactPerson.getPerson().getId());
    }

    @Test
    @TransactionScoped
    void save() {
        long sizeBefore = contactPersonRepository.count();
        insertDummyContactPersonThatIsHisHerOwnContactPerson();
        long sizeAfter = contactPersonRepository.count();

        assertEquals(sizeBefore + 1, sizeAfter);
    }

    @Test
    @TransactionScoped
    void delete() {
        ContactPerson contactPerson = insertDummyContactPersonThatIsHisHerOwnContactPerson();

        long sizeBefore = contactPersonRepository.count();
        service.delete(contactPerson);
        long sizeAfter = contactPersonRepository.count();

        assertEquals(sizeBefore - 1, sizeAfter);
    }
}