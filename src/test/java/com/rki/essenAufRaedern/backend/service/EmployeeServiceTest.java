package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Employee;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.AddressRepository;
import com.rki.essenAufRaedern.backend.repository.EmployeeRepository;
import com.rki.essenAufRaedern.backend.repository.KitchenRepository;
import com.rki.essenAufRaedern.backend.repository.PersonRepository;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.TransactionScoped;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EmployeeServiceTest {

    @Autowired
    EmployeeService service;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    KitchenRepository kitchenRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PersonRepository personRepository;

    private Employee createDummyEmployee() {
        Employee employee = TestUtil.createDummyEmployee();
        addressRepository.save(employee.getKitchen().getAddress());
        kitchenRepository.save(employee.getKitchen());

        Person person = employee.getPerson();
        person.setEmployee(null);
        personRepository.save(person);
        employee.setPerson(person);

        return employee;
    }

    private Employee insertDummyEmployee() {
        Employee employee = createDummyEmployee();
        service.save(employee);

        return employee;
    }

    @Test
    @TransactionScoped
    void findAll() {
        Employee employee = insertDummyEmployee();
        assertNotEquals(0, service.findAll().stream().filter(item -> item.getId().equals(employee.getId())).count());
    }

    @Test
    @TransactionScoped
    void findByPersonId() {
        Employee employee = insertDummyEmployee();
        Person person = employee.getPerson();

        List<Employee> employeeList = service.findByPersonId(person.getId());
        assertNotEquals(0, employeeList.size());
    }

    @Test
    @TransactionScoped
    void count() {
        insertDummyEmployee();
        assertNotEquals(0, service.count());
    }

    @Test
    @TransactionScoped
    void delete() {
        Employee employee = insertDummyEmployee();
        service.delete(employee);

        Optional<Employee> deletedEmployee = employeeRepository.findById(employee.getId());
        assertFalse(deletedEmployee.isEmpty());

        assertEquals(Status.INACTIVE, deletedEmployee.get().getStatus());
    }

    @Test
    @TransactionScoped
    void save() {
        long sizeBefore = service.count();
        insertDummyEmployee();
        long sizeAfter = service.count();

        assertEquals(sizeBefore + 1, sizeAfter);
    }
}