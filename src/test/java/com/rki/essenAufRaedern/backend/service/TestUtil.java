package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;

import java.util.Date;

public class TestUtil {

    public static Person createDummyPerson() {
        Person person = new Person();
        person.setFirstName("Max");
        person.setLastName("Muster");
        person.setPersonType(PersonType.CLIENT);
        person.setPhoneNumber("1");
        person.setBirthdate(new Date());
        person.setStatus(Status.ACTIVE);

        return person;
    }

    public static Address createDummyAddress() {
        Address address = new Address();
        address.setStatus(Status.ACTIVE);
        address.setCountry("dd");
        address.setFloor("1");
        address.setCity("a");
        address.setStreet("d");
        address.setHouseNumber("2");
        address.setZipCode("5");

        return address;
    }

    public static Kitchen createDummyKitchen() {
        Kitchen kitchen = new Kitchen();
        kitchen.setStatus(Status.ACTIVE);
        kitchen.setName("DUMMY");
        kitchen.setAddress(createDummyAddress());

        return kitchen;
    }

    public static Employee createDummyEmployee() {
        Employee employee = new Employee();
        employee.setStatus(Status.ACTIVE);
        employee.setPerson(createDummyPerson());
        employee.setKitchen(createDummyKitchen());

        return employee;
    }

    public static OrderInformation createDummyOrderInformation() {
        OrderInformation orderInformation = new OrderInformation();
        orderInformation.setDt_form(new Date());
        orderInformation.setMonday(Status.ACTIVE);
        orderInformation.setTuesday(Status.ACTIVE);
        orderInformation.setWednesday(Status.ACTIVE);
        orderInformation.setThursday(Status.ACTIVE);
        orderInformation.setFriday(Status.ACTIVE);
        orderInformation.setSaturday(Status.ACTIVE);
        orderInformation.setSunday(Status.ACTIVE);
        orderInformation.setStatus(Status.ACTIVE);
        Person person = TestUtil.createDummyPerson();
        orderInformation.setPerson(person);

        return orderInformation;
    }

    public static Order createDummyOrder() {
        Order order = new Order();
        order.setKitchen(createDummyKitchen());
        order.setPerson(createDummyPerson());
        order.setDt(new Date());
        order.setStatus(Status.ACTIVE);

        return order;
    }

    public static User createDummyUser() {
        Person userPerson = createDummyPerson();
        User user = new User();
        user.setUsername("TestUser");
        user.setEmail(userPerson.getFirstName() + "." + userPerson.getLastName() + "@test.at");
        user.setStatus(Status.ACTIVE);
        user.setPerson(userPerson);
        user.setPassword("changeMe");

        return user;
    }

}
