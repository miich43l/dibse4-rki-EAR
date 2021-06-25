package com.rki.essenAufRaedern.backend.utility;

import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.repository.*;
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
        addressRepository.save(kitchenAddress);

        Kitchen kitchen = new Kitchen();
        kitchen.setName("Kitchen Altersheim Ötz");
        kitchen.setAddress(kitchenAddress);
        kitchen.setStatus(Status.Active);
        kitchenRepository.save(kitchen);

        Person driverPerson = new Person();
        driverPerson.setStatus(Status.Active);
        driverPerson.setFirstName("Max");
        driverPerson.setLastName("Vollgas");
        driverPerson.setBirthdate(new Date());
        driverPerson.setPhoneNumber("+43 664 12345678");
        driverPerson.setPersonType(PersonType.Driver);
        personRepository.save(driverPerson);

        Employee driver = new Employee();
        driver.setStatus(Status.Active);
        driver.setKitchen(kitchen);
        driver.setPerson(driverPerson);
        employeeRepository.save(driver);

        List<Employee> employees = employeeRepository.findAll();
        System.out.println("Employees: " + employees.get(0).getKitchen());

        List<Address> addresses = createAddressData();
        int orderCtn = 0;
        int maxOrders = 9;

        // Create persons:
        {
            // PersonType: Administration, Kitchen, Driver, Client, ContactPerson, LocalCommunity

            List<String> personStrings = new ArrayList<>();
            personStrings.add("Arian;Schneider;Client");
            personStrings.add("Armin;Schuster;Client");
            personStrings.add("Arthur;Schwarz;Client");
            personStrings.add("ASMIN;Stadler;Client");
            personStrings.add("Aurelia;Steiner;Client");
            personStrings.add("AYLIN;Strasser;Client");
            personStrings.add("AZRA;Wagner;Client");
            personStrings.add("BASTIAN;Wallner;Client");
            personStrings.add("Ben;Weber;Client");
            personStrings.add("Benedikt;Weiss;Client");
            personStrings.add("Benjamin;Wieser;Client");
            personStrings.add("BERAT;Wimmer;Client");
            personStrings.add("Sebastian;Vettel;Driver");
            personStrings.add("Maria;Mustermann;ContactPerson");
            personStrings.add("Christina;Binder;ContactPerson");
            personStrings.add("CHRISTOF;Brunner;ContactPerson");
            personStrings.add("Christoph;Ebner;ContactPerson");
            personStrings.add("CLARA;Eder;ContactPerson");
            personStrings.add("CLAUDIA;Egger;ContactPerson");
            personStrings.add("Clemens;Fischer;ContactPerson");


            List<Person> clients = new ArrayList<>();
            for (int nP = 0; nP < personStrings.size(); nP++) {
                String name = personStrings.get(nP);
                String[] elements = name.split(";");
                String firstName = elements[0];
                String lastName = elements[1];
                String typeString = elements[2];
                PersonType type = PersonType.fromString(typeString);

                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.HOUR, -(400000 + new Random().nextInt(300000)));

                Person person = new Person();
                person.setFirstName(firstName);
                person.setLastName(lastName);
                person.setBirthdate(c.getTime());
                person.setAddress(addresses.get(nP));
                person.setStatus(Status.Active);
                person.setPersonType(type);

                // Create an order if type == Client:
                switch (type) {
                    case Administration -> {
                    }
                    case Kitchen -> {
                    }
                    case Driver -> {
                        personRepository.save(person);

                        Employee employee = new Employee();
                        employee.setPerson(person);
                        employee.setKitchen(kitchen);
                        employee.setStatus(Status.Active);

                        employeeRepository.save(employee);
                    }
                    case Client -> {
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
                    case ContactPerson -> {
                        ContactPerson contactPerson = new ContactPerson();
                        contactPerson.setContactPersonType(ContactPersonType.FamilyMember);
                        contactPerson.setPerson(person);
                        contactPerson.setContactPersonFrom(clients.get(new Random().nextInt(clients.size())));
                        personRepository.save(person);
                        contactPersonRepository.save(contactPerson);
                    }
                    case LocalCommunity -> {
                    }
                }
            }
        }
    }

    private void createRandomOrderInformationForPerson(Person person) {
        OrderInformation orderInformation = new OrderInformation();
        orderInformation.setPerson(person);
        orderInformation.setMonday(new Random().nextBoolean() ? Status.Active : Status.Inactive);
        orderInformation.setTuesday(new Random().nextBoolean() ? Status.Active : Status.Inactive);
        orderInformation.setWednesday(new Random().nextBoolean() ? Status.Active : Status.Inactive);
        orderInformation.setThursday(new Random().nextBoolean() ? Status.Active : Status.Inactive);
        orderInformation.setFriday(new Random().nextBoolean() ? Status.Active : Status.Inactive);
        orderInformation.setSaturday(new Random().nextBoolean() ? Status.Active : Status.Inactive);
        orderInformation.setSunday(new Random().nextBoolean() ? Status.Active : Status.Inactive);
        orderInformationRepository.save(orderInformation);
    }

    private void createOrdersForPerson(Kitchen kitchen, Person person, int nDayOffset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, nDayOffset);

        Order order = new Order();
        order.setKitchen(kitchen);
        order.setPerson(person);
        order.setDt(cal.getTime());
        order.setStatus(Status.Active);
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
            info.setPerson(person);

            additionalInformationRepository.save(info);

            System.out.println("Created additional information: " + info.getValue() + " for: " + person.getFullName());
        }
    }
}
