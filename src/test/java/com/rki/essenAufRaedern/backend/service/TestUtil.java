package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Employee;
import com.rki.essenAufRaedern.backend.entity.Kitchen;
import com.rki.essenAufRaedern.backend.entity.Person;
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

}
