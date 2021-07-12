package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.AdditionalInformationRepository;
import com.rki.essenAufRaedern.backend.repository.PersonRepository;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import javax.transaction.TransactionScoped;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AdditionalInformationServiceTest {

    @Autowired
    AdditionalInformationService service;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    AdditionalInformationRepository additionalInformationRepository;

    private Person dummyPerson;
    private AdditionalInformation dummyInfo;

    private AdditionalInformation createAdditionalInfoForDummyPerson() {
        AdditionalInformation info = new AdditionalInformation();
        info.setPerson(dummyPerson);
        info.setValue("a");
        info.setStatus(Status.ACTIVE);
        info.setInformationType(InformationType.DRIVER);

        return info;
    }

    @BeforeEach
    public void createDummyData() {
        System.out.println("BeforeEach: createDummyData");

        dummyPerson = new Person();
        dummyPerson.setLastName("x");
        dummyPerson.setFirstName("x");
        dummyPerson.setPersonType(PersonType.CLIENT);
        dummyPerson.setStatus(Status.ACTIVE);
        dummyPerson.setBirthdate(new Date());
        dummyPerson.setPhoneNumber("0");
        personRepository.save(dummyPerson);

        dummyInfo = new AdditionalInformation();
        dummyInfo.setPerson(dummyPerson);
        dummyInfo.setInformationType(InformationType.DRIVER);
        dummyInfo.setValue("x");
        additionalInformationRepository.save(dummyInfo);
    }

    @AfterEach
    public void deleteDummyData() {
        System.out.println("AfterEach: createDummyData");

        try {
            additionalInformationRepository.delete(dummyInfo);
            personRepository.delete(dummyPerson);
        }catch(Exception ignored) {

        }
    }

    @Test
    void findAll() {
        List<AdditionalInformation> allItems = service.findAll();
        List<AdditionalInformation> filteredItems = allItems.stream().filter(item -> item.getId().equals(dummyInfo.getId())).collect(Collectors.toList());
        assertEquals(1, filteredItems.size());
    }

    @Test
    void findByPersonId() {
        List<AdditionalInformation> infoForPerson = service.findByPersonId(dummyPerson.getId());
        assertEquals(1, infoForPerson.size());
        assertEquals(dummyInfo.getId(), infoForPerson.get(0).getId());
    }

    @Test
    void count() {
        List<AdditionalInformation> allItems = service.findAll();
        assertNotEquals(0, allItems.size());
    }

    @Test
    @TransactionScoped
    void delete() {
        List<AdditionalInformation> allItems = service.findAll();
        int sizeBefore = allItems.size();

        service.delete(dummyInfo);

        Optional<AdditionalInformation> info = additionalInformationRepository.findById(dummyInfo.getId());

        assertFalse(info.isEmpty());
        assertEquals(Status.INACTIVE, info.get().getStatus());
    }

    @Test
    @TransactionScoped
    void save() {
        List<AdditionalInformation> allItems = service.findAll();
        int sizeBefore = allItems.size();

        AdditionalInformation info = createAdditionalInfoForDummyPerson();
        service.save(info);

        assertEquals(sizeBefore + 1, service.findAll().size());
    }

    @Test
    void findForPersonAndType() {
        // driver
        {
            List<AdditionalInformation> infoForPerson = service.findForPersonAndType(dummyPerson.getId(), InformationType.DRIVER);
            assertEquals(1, infoForPerson.size());
            assertEquals(dummyInfo.getId(), infoForPerson.get(0).getId());
        }

        // kitchen
        {
            List<AdditionalInformation> infoForPerson = service.findForPersonAndType(dummyPerson.getId(), InformationType.KITCHEN);
            assertEquals(0, infoForPerson.size());
        }
    }
}