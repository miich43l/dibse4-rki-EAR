package com.rki.essenAufRaedern.backend.utility;

import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.repository.*;
import com.rki.essenAufRaedern.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author thomaswiedmann and andreasoberhofer
 * The utility class loads a set of testdata into the h2 database:
 *  - 1 kitchen with kitchenAddress,
 *  - 1 driver with person and employee data
 *  - some addresses in the Ötztal local communities
 *  - some clients with random birthdate (btw 35 and 45 years ago) and a random adress
 *  - somecontactpersons (ContactPersonType.FamilyMember) with random birthdate and adress
 *  - every client gets one random contactperson and one order for today
 */
@Component
public class TestDataLoader {

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
    private OrderInformationRepository orderInformationRepository;

    @Autowired
    private ContactPersonRepository contactPersonRepository;

    @Autowired
    private AdditionalInformationRepository additionalInformationRepository;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void loadData() {
        System.out.println("Create test data...");

        Address kitchenAddress = new Address();
        kitchenAddress.setCity("Oetz");
        kitchenAddress.setHouseNumber("9");
        kitchenAddress.setStreet("Platzleweg");
        kitchenAddress.setZipCode("6433");
        kitchenAddress.setFloor("1");
        kitchenAddress.setCountry("Österreich");
        kitchenAddress.setStatus(Status.ACTIVE);
        addressRepository.save(kitchenAddress);

        Kitchen kitchen = new Kitchen();
        kitchen.setName("Altersheim Ötz");
        kitchen.setAddress(kitchenAddress);
        kitchen.setStatus(Status.ACTIVE);
        kitchenRepository.save(kitchen);

        Person driverPerson = new Person();
        driverPerson.setStatus(Status.ACTIVE);
        driverPerson.setFirstName("Max");
        driverPerson.setLastName("Vollgas");
        driverPerson.setBirthdate(new Date());
        driverPerson.setPhoneNumber("+43 664 12345678");
        driverPerson.setPersonType(PersonType.DRIVER);
        personRepository.save(driverPerson);

        Employee driver = new Employee();
        driver.setStatus(Status.ACTIVE);
        driver.setKitchen(kitchen);
        driver.setPerson(driverPerson);
        employeeRepository.save(driver);

        // driver username = "Max_Vollgas"

        User user = new User();
        user.setUsername(driverPerson.getFirstName() + "_" + driverPerson.getLastName());
        user.setEmail(driverPerson.getFirstName() + "." + driverPerson.getLastName() + "@rki.at");
        user.setStatus(Status.ACTIVE);
        user.setPerson(driverPerson);
        user.setPassword("changeMe");
        userService.save(user);

        List<Employee> employees = employeeRepository.findAll();
        System.out.println("Employees: " + employees.get(0).getKitchen());

        List<Address> addresses = createAddressData();
        int orderCtn = 0;
        int maxOrders = 9;

        //Add users
        addUser("test", "Administration", "+123", "test@rki.com", PersonType.ADMINISTRATION, "admin", "changeMe");
        addUser("test", "kitchen", "+123", "test@rki.com", PersonType.KITCHEN, "kitchen", "changeMe");
        addUser("test", "contactPerson", "+123", "test@rki.com", PersonType.CONTACT_PERSON, "contactPerson", "changeMe");
        addUser("test", "localCommunity", "+123", "test@rki.com", PersonType.LOCAL_COMMUNITY, "localCommunity", "changeMe");
        addUser("test", "developer", "+123", "test@rki.com", PersonType.DEVELOPER, "developer", "changeMe");


        // Create persons:
        {
            List<String> personStrings = new ArrayList<>();
            personStrings.add("Arian;Schneider;Client;+123");
            personStrings.add("Armin;Schuster;Client;+123");
            personStrings.add("Arthur;Schwarz;Client;+123");
            personStrings.add("ASMIN;Stadler;Client;+123");
            personStrings.add("Aurelia;Steiner;Client;+123");
            personStrings.add("AYLIN;Strasser;Client;+123");
            personStrings.add("AZRA;Wagner;Client;+123");
            personStrings.add("BASTIAN;Wallner;Client;+123");
            personStrings.add("Ben;Weber;Client;+123");
            personStrings.add("Benedikt;Weiss;Client;+123");
            personStrings.add("Benjamin;Wieser;Client;+123");
            personStrings.add("BERAT;Wimmer;Client;+123");
            personStrings.add("Sebastian;Vettel;Driver;+123");
            personStrings.add("Christina;Binder;ContactPerson;+123");
            personStrings.add("CHRISTOF;Brunner;ContactPerson;+123");
            personStrings.add("Christoph;Ebner;ContactPerson;+123");
            personStrings.add("CLARA;Eder;ContactPerson;+123");
            personStrings.add("CLAUDIA;Egger;ContactPerson;+123");
            personStrings.add("Clemens;Fischer;ContactPerson;+123");


            List<Person> clients = new ArrayList<>();
            for (int nP = 0; nP < personStrings.size(); nP++) {
                String name = personStrings.get(nP);
                String[] elements = name.split(";");
                String firstName = elements[0];
                String lastName = elements[1];
                String typeString = elements[2];
                String phoneNumber = elements[3];

                PersonType type = PersonType.fromString(typeString);

                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.HOUR, -(400000 + new Random().nextInt(300000)));

                Person person = new Person();
                person.setFirstName(firstName);
                person.setLastName(lastName);
                person.setBirthdate(c.getTime());
                person.setAddress(addresses.get(nP));
                person.setPhoneNumber(phoneNumber);
                person.setStatus(Status.ACTIVE);
                person.setPersonType(type);

                // Create an order if type == Client:
                switch (type) {
                    case ADMINISTRATION, KITCHEN, LOCAL_COMMUNITY, DEVELOPER -> {
                    }
                    case DRIVER -> {
                        personRepository.save(person);

                        Employee employee = new Employee();
                        employee.setPerson(person);
                        employee.setKitchen(kitchen);
                        employee.setStatus(Status.ACTIVE);

                        employeeRepository.save(employee);
                    }
                    case CLIENT -> {
                        personRepository.save(person);

                        // do not always create an order...
                        if(orderCtn < maxOrders) {
                            for(int nDayOffset = 0; nDayOffset < 7; nDayOffset++) {
                                if(nDayOffset > 0 && !new Random().nextBoolean()) {
                                    continue;
                                }

                                createOrdersForPerson(kitchen, person, nDayOffset);
                            }

                            orderCtn++;
                        }

                        clients.add(person);
                        createRandomAdditionalInformationForPerson(person);
                        createRandomOrderInformationForPerson(person);
                    }
                    case CONTACT_PERSON -> {
                        Person client = clients.get(new Random().nextInt(clients.size()));

                        ContactPerson contactPerson = new ContactPerson();
                        contactPerson.setContactPersonType(ContactPersonType.FAMILY_MEMBER);
                        contactPerson.setPerson(client);

                        client.addContactPerson(contactPerson);
                        person.addContactPersonFrom(contactPerson);

                        personRepository.save(client);
                        personRepository.save(person);
                        contactPersonRepository.save(contactPerson);

                        System.out.println("Contact person from: " + client.getFullName() + " is: " + client.getContactPersons().iterator().next().getContactPersonFrom().getFullName());
                    }
                }
            }
        }

        System.out.println("Additional informations: ");
        additionalInformationRepository.findAll().forEach(item -> {
            System.out.println("    => " + item.getInformationType() + " -- " + item.getValue() + " ID: " + item.getId());
        });


        System.out.println("Persons: ");
        personRepository.findAll().forEach(item -> {
            System.out.println("    => " + item.getFullName() + " ID: " + item.getId());
            item.getAllAdditionalInformation().forEach(info -> {
                System.out.println("        => " + info.getInformationType() + " -- " + info.getValue() + " ID: " + info.getId());
            });
        });

    }

    private void createRandomOrderInformationForPerson(Person person) {
        OrderInformation orderInformation = new OrderInformation();
        orderInformation.setPerson(person);
        orderInformation.setMonday(new Random().nextBoolean() ? Status.ACTIVE : Status.INACTIVE);
        orderInformation.setTuesday(new Random().nextBoolean() ? Status.ACTIVE : Status.INACTIVE);
        orderInformation.setWednesday(new Random().nextBoolean() ? Status.ACTIVE : Status.INACTIVE);
        orderInformation.setThursday(new Random().nextBoolean() ? Status.ACTIVE : Status.INACTIVE);
        orderInformation.setFriday(new Random().nextBoolean() ? Status.ACTIVE : Status.INACTIVE);
        orderInformation.setSaturday(new Random().nextBoolean() ? Status.ACTIVE : Status.INACTIVE);
        orderInformation.setSunday(new Random().nextBoolean() ? Status.ACTIVE : Status.INACTIVE);
        Calendar cal = Calendar.getInstance();
        orderInformation.setDt_form(cal.getTime());
        orderInformation.setStatus(Status.ACTIVE);
        orderInformationRepository.save(orderInformation);
        person.addOrderInformation(orderInformation);
    }

    public void addUser(String firstName, String LastName, String phoneNumber, String email, PersonType personType, String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setStatus(Status.ACTIVE);
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(LastName);
        person.setPhoneNumber(phoneNumber);
        person.setPersonType(personType);
        person.setStatus(Status.ACTIVE);
        personRepository.save(person);
        user.setPerson(person);
        user.setPassword(password);
        userService.save(user);
    }

    private void createOrdersForPerson(Kitchen kitchen, Person person, int nDayOffset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, nDayOffset);

        Order order = new Order();
        order.setKitchen(kitchen);
        order.setPerson(person);
        order.setDt(cal.getTime());
        order.setStatus(Status.ACTIVE);
        orderRepository.save(order);
    }

    private List<Address> createAddressData() {
        // Create addresses:
        List<Address> addresses = new ArrayList<>();
        {
            List<String> addressStrings = new ArrayList<>();
/*
            addressStrings.add("Österreich;6020;Innsbruck;Anichstraße;26;1");
            addressStrings.add("Österreich;6020;Innsbruck;Wopfnerstraße;48;1");
            addressStrings.add("Österreich;6067;Absam;Schulstraße;4;1");
            addressStrings.add("Österreich;6167;Neustift im Stubaital;Fichtenweg;6;1");
*/
            addressStrings.add("Austria;6432;Sautens;Wiedumgasse;3;0");
            addressStrings.add("Austria;6432;Sautens;Dorfstrasse;49;0");
            addressStrings.add("Austria;6432;Sautens;Silbergasse;6b;0");
            addressStrings.add("Austria;6432;Sautens;Lafeld;9;0");
            addressStrings.add("Austria;6432;Sautens;Kirchweg;19;0");
            addressStrings.add("Austria;6432;Sautens;Pirchhof;68;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Ambergstrasse;20;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Waldstrasse;12;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Sandbichlweg;17;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Bahnrain;16a;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Birkenstrasse;9;0");

            addressStrings.add("Austria;6450;Sölden;Uferweg;1;2a");         // Route 1
            addressStrings.add("Austria;6450;Zwieselstein;Roanweg;14;1");
            addressStrings.add("Austria;6450;Sölden;Alpenweg;4;1");
            addressStrings.add("Austria;6450;Sölden;Hainbachweg;19;1");
            addressStrings.add("Austria;6450;Sölden;Dorfblickweg;6;0");
            addressStrings.add("Austria;6450;Sölden;Platterstraße;17;keine Angabe");
            addressStrings.add("Austria;6450;Sölden;Blumenweg;9;x");
            addressStrings.add("Austria;6450;Sölden;Wildmoosstraße;4;0");
            addressStrings.add("Austria;6450;Sölden;Gewerbestraße;2;0");
            addressStrings.add("Austria;6456;Obergurgl;Gaisbergweg;23;0");
            addressStrings.add("Austria;6456;Obergurgl;Kressbrunnenweg;6a;0");
            addressStrings.add("Austria;6456;Obergurgl;Schlossweg;7;0");
            addressStrings.add("Austria;6444;Längenfeld;Unterlängenfeld;88a;0");
            addressStrings.add("Austria;6444;Längenfeld;Unterlängenfeld;154;0");
            addressStrings.add("Austria;6444;Längenfeld;Oberlängenfeld;42;0");
            addressStrings.add("Austria;6444;Längenfeld;Oberlängenfeld;100a;0");
            addressStrings.add("Austria;6444;Längenfeld;Unterlängenfeld;91a;0");
            addressStrings.add("Austria;6441;Umhausen;Sandgasse;5;0");
            addressStrings.add("Austria;6441;Umhausen;Lehngasse;32;0");
            addressStrings.add("Austria;6441;Umhausen;Achrainweg;28;0");
            addressStrings.add("Austria;6432;Sautens;Wiedumgasse;3;0");
            addressStrings.add("Austria;6432;Sautens;Dorfstrasse;49;0");
            addressStrings.add("Austria;6432;Sautens;Silbergasse;6b;0");
            addressStrings.add("Austria;6432;Sautens;Lafeld;9;0");
            addressStrings.add("Austria;6432;Sautens;Kirchweg;19;0");
            addressStrings.add("Austria;6432;Sautens;Pirchhof;68;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Ambergstrasse;20;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Waldstrasse;12;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Sandbichlweg;17;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Bahnrain;16a;0");
            addressStrings.add("Austria;6430;Ötztal Bahnhof;Birkenstrasse;9;0");
            addressStrings.add("Austria;6441;Umhausen;Tumpen;250;0");
            addressStrings.add("Austria;6441;Umhausen;Tumpen;228;2");
            addressStrings.add("Austria;6441;Umhausen;Tumpen;71a;0");
            addressStrings.add("Austria;6441;Umhausen;Tumpen;120;1");

            addressStrings.forEach(line -> {
                String[] elements = line.split(";");
                Address address = new Address();
                address.setCity(elements[2]);
                address.setZipCode(elements[1]);
                address.setStreet(elements[3]);
                address.setHouseNumber(elements[4]);
                address.setFloor(elements[5]);
                address.setCountry(elements[0]);
                address.setStatus(Status.ACTIVE);


                addresses.add(address);
            });
            addressRepository.saveAll(addresses);
        }

        return addresses;
    }

    private void createRandomAdditionalInformationForPerson(Person person) {
        List<String> additionalInformations = new ArrayList<>();
        additionalInformations.add("Schlüssen links am Fenster;Driver");
        additionalInformations.add("Am Fenster klopfen;Driver");

        additionalInformations.add("Vegetarisch;Kitchen");
        additionalInformations.add("Kleine Portion;Kitchen");
        additionalInformations.add("Keine Vorspeise;Kitchen");

        int numberOfInfosToCreate = new Random().nextInt(4);
        if(additionalInformations.size() < numberOfInfosToCreate) {
            numberOfInfosToCreate = additionalInformations.size();
        }

        List<Integer> lstInfos = new ArrayList<>();
        for(int nHowMany = 0; nHowMany < numberOfInfosToCreate; nHowMany++) {
            int nRandIdx = new Random().nextInt(additionalInformations.size());

            while(lstInfos.contains(nRandIdx)) {
                nRandIdx = new Random().nextInt(additionalInformations.size());
            }

            lstInfos.add(nRandIdx);

            String line = additionalInformations.get(nRandIdx);
            String[] elements = line.split(";");
            AdditionalInformation info = new AdditionalInformation();
            info.setValue(elements[0]);
            info.setInformationType(InformationType.fromString(elements[1]));
            info.setStatus(Status.ACTIVE);
            person.addAdditionalInformation(info);

            additionalInformationRepository.save(info);

            System.out.println("Created additional information: " + info.getValue() + " for: " + person.getFullName());
        }
    }
}
