package com.rki.essenAufRaedern.backend.utility;

import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

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

    @PostConstruct
    public void loadData() {
        System.out.println("Create test data...");

        Address kitchenAddress = new Address();
        kitchenAddress.setCity("Innsbruck");
        kitchenAddress.setHouseNumber("123");
        kitchenAddress.setStreet("Tiergartenstraße");
        kitchenAddress.setZipCode("6020");
        kitchenAddress.setFloor("1");
        kitchenAddress.setCountry("Österreich");
        addressRepository.save(kitchenAddress);

        Kitchen kitchen = new Kitchen();
        kitchen.setName("The Kitchen");
        kitchen.setAddress(kitchenAddress);
        kitchen.setStatus(Status.Active);
        kitchenRepository.save(kitchen);

        List<Address> addresses = createAddressData();

        // Create persons:
        {
            List<String> personStrings = new ArrayList<>();
            personStrings.add("Max;Mustermann;Klient");
            personStrings.add("Anna;Mustermann;Klient");
            personStrings.add("Hans;Dummy;Fahrer");
            personStrings.add("Maria;Mustermann;ContactPerson");

            List<Person> clients = new ArrayList<>();
            for(int nP = 0; nP < personStrings.size(); nP++) {
                String name = personStrings.get(nP);
                String[] elements = name.split(";");
                String firstName = elements[0];
                String lastName = elements[1];
                String type = elements[2];

                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.HOUR, new Random().nextInt(2000));

                Person person = new Person();
                person.setFirstName(firstName);
                person.setLastName(lastName);
                person.setBirthdate(c.getTime());
                person.setAddress(addresses.get(nP));
                person.setStatus(Status.Active);

                // Create an order if type == Klient:
                if(type.equals("Klient")) {
                    person.setPersonType(PersonType.Client);
                    personRepository.save(person);

                    Order order = new Order();
                    order.setKitchen(kitchen);
                    order.setPerson(person);
                    order.setDt(new Date()); // today
                    orderRepository.save(order);

                    clients.add(person);
                } else if(type.equals("Fahrer")) {
                    person.setPersonType(PersonType.Driver);
                    personRepository.save(person);

                    Employee employee = new Employee();
                    employee.setPerson(person);
                    employee.setKitchen(kitchen);
                    employee.setStatus(Status.Active);

                    employeeRepository.save(employee);
                }/* else if(type.equals("ContactPerson")) {
                    ContactPerson contactPerson = new ContactPerson();
                    contactPerson.setContactPersonFrom(clients.get(new Random().nextInt(clients.size())));

                    personRepository.save(person);
                }*/
            }
        }
    }

    private List<Address> createAddressData() {
        // Create addresses:
        List<Address> addresses = new ArrayList<>();
        {
            List<String> addressStrings = new ArrayList<>();
            addressStrings.add("Österreich;6020;Innsbruck;Anichstraße;26;1");
            addressStrings.add("Österreich;6020;Innsbruck;Wopfnerstraße;48;1");
            addressStrings.add("Österreich;6067;Absam;Schulstraße;4;1");
            addressStrings.add("Österreich;6167;Neustift im Stubaital;Fichtenweg;6;1");

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

}
