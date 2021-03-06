package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.repository.*;
import com.rki.essenAufRaedern.backend.utility.ContactPersonType;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BackendTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private KitchenRepository kitchenRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ContactPersonRepository contactPersonRepository;

    @Autowired
    private AdditionalInformationRepository additionalInformationRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private KitchenService kitchenService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ContactPersonService contactPersonService;

    @Autowired
    private AdditionalInformationService additionalInformationService;

    private Long addressId;
    private Long kitchenId;
    private Long driverPersonId;
    private Long driverEmployeeId;
    private Long contactPersonId;
    private Long additionalInformationId;
    private Long clientPersonId;
    private Long contactPersonRepositoryId;
    private Long orderId;

    @Test
    void checkDBEntities() {
        createData();

        Optional<Person> tDriver;
        tDriver = personService.findByFirstnameAndLastname("Test", "Driver").stream().findFirst();
        Kitchen tKitchen;
        tKitchen = employeeService.findByPersonId(tDriver.get().getId()).get(0).getKitchen();
        Optional<Order> tOrder = orderService.getOrdersForKitchenAndDay(tKitchen.getId(), new Date())
                .stream()
                .filter(o -> o.getPerson().getFirstName().equals("Test") && o.getPerson().getLastName().equals("client"))
                .findFirst();
        Optional<ContactPerson> tContactPerson = contactPersonService.getContactPersons(tOrder.get()
                .getPerson()
                .getId())
                .stream()
                .filter(cp -> cp.getPerson().getFirstName().equals("Test") && cp.getPerson().getLastName().equals("contactPerson"))
                .findFirst();
        List<AdditionalInformation> tAdditionalInformation;
        tAdditionalInformation = additionalInformationService.findByPersonId(tContactPerson.get()
                .getPerson()
                .getId());
        assertEquals("all test's passed", tAdditionalInformation.get(0).getValue());

        deleteData();
        Exception exception = new Exception();
        try {
            personService.findByFirstnameAndLastname("Test", "Driver").stream().findFirst().get().getId();
        } catch (Exception e) {
            exception = e;
        }
        assertEquals("No value present", exception.getMessage());
    }

    private void createData() {
        Address address = new Address();
        address.setCity("Test");
        address.setHouseNumber("1");
        address.setStreet("Testweg");
        address.setZipCode("6020");
        address.setFloor("1");
        address.setCountry("??sterreich");
        address.setStatus(Status.ACTIVE);
        addressRepository.save(address);
        addressId = address.getId();
        Kitchen kitchen = new Kitchen();
        kitchen.setName("Test Kitchen");
        kitchen.setAddress(address);
        kitchen.setStatus(Status.ACTIVE);
        kitchenRepository.save(kitchen);
        kitchenId = kitchen.getId();
        Person driverPerson = new Person();
        driverPerson.setStatus(Status.ACTIVE);
        driverPerson.setFirstName("Test");
        driverPerson.setLastName("Driver");
        driverPerson.setBirthdate(new Date());
        driverPerson.setPhoneNumber("+123");
        driverPerson.setPersonType(PersonType.DRIVER);
        personRepository.save(driverPerson);
        driverPersonId = driverPerson.getId();
        Employee driver = new Employee();
        driver.setPerson(driverPerson);
        driver.setKitchen(kitchen);
        driver.setStatus(Status.ACTIVE);
        employeeRepository.save(driver);
        driverEmployeeId = driver.getId();
        Person contactPerson = new Person();
        contactPerson.setFirstName("Test");
        contactPerson.setLastName("contactPerson");
        contactPerson.setBirthdate(new Date());
        contactPerson.setAddress(address);
        contactPerson.setPhoneNumber("+123");
        contactPerson.setStatus(Status.ACTIVE);
        contactPerson.setPersonType(PersonType.CONTACT_PERSON);
        personRepository.save(contactPerson);
        contactPersonId = contactPerson.getId();
        AdditionalInformation additionalInformation = new AdditionalInformation();
        additionalInformation.setPerson(contactPerson);
        additionalInformation.setInformationType(InformationType.DRIVER);
        additionalInformation.setValue("all test's passed");
        additionalInformation.setStatus(Status.ACTIVE);
        additionalInformationService.save(additionalInformation);
        additionalInformationId = additionalInformation.getId();
        Person client = new Person();
        client.setFirstName("Test");
        client.setLastName("client");
        client.setBirthdate(new Date());
        client.setAddress(address);
        client.setPhoneNumber("+123");
        Set cPerson = new HashSet<Person>();
        cPerson.add(contactPerson);
        client.setContactPerson(cPerson);
        client.setStatus(Status.ACTIVE);
        client.setPersonType(PersonType.CLIENT);
        personRepository.save(client);
        clientPersonId = client.getId();
        ContactPerson contactPersonR = new ContactPerson();
        contactPersonR.setPerson(contactPerson);
        contactPersonR.setContactPersonFrom(client);
        contactPersonR.setContactPersonType(ContactPersonType.NEIGHBOR);
        contactPersonRepository.save(contactPersonR);
        contactPersonRepositoryId = contactPersonR.getId();
        Order order = new Order();
        order.setKitchen(kitchen);
        order.setPerson(client);
        order.setDt(new Date());
        order.setStatus(Status.ACTIVE);
        orderRepository.save(order);
        orderId = order.getId();
    }

    private void deleteData() {
        Order order = orderRepository.getOne(orderId);
        Employee driver = employeeRepository.getOne(driverEmployeeId);
        Kitchen kitchen = kitchenRepository.getOne(kitchenId);
        Person driverPerson = personRepository.getOne(driverPersonId);
        AdditionalInformation additionalInformation = additionalInformationRepository.getOne(additionalInformationId);
        ContactPerson contactPersonR = contactPersonRepository.getOne(contactPersonRepositoryId);
        Person client = personRepository.getOne(clientPersonId);
        Person contactPerson = personRepository.getOne(contactPersonId);
        Address address = addressRepository.getOne(addressId);
        orderRepository.delete(order);
        employeeRepository.delete(driver);
        kitchenRepository.delete(kitchen);
        personRepository.delete(driverPerson);
        additionalInformationRepository.delete(additionalInformation);
        contactPersonRepository.delete(contactPersonR);
        personRepository.delete(client);
        personRepository.delete(contactPerson);
        addressRepository.delete(address);
    }
}