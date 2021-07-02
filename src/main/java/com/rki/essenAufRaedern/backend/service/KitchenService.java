package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Employee;
import com.rki.essenAufRaedern.backend.entity.Kitchen;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.*;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The service class for the kitchen database table.
 * @getDriver(kitchenId) get all drivers for kitchen
 */

@Service
public class KitchenService {
    private static final Logger LOGGER = Logger.getLogger(KitchenService.class.getName());
    private final KitchenRepository kitchenRepository;
    private final PersonRepository personRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;


    public KitchenService(KitchenRepository kitchenRepository, PersonRepository personRepository, EmployeeRepository employeeRepository, OrderRepository orderRepository) {
        this.kitchenRepository = kitchenRepository;
        this.personRepository = personRepository;
        this.employeeRepository = employeeRepository;
        this.orderRepository = orderRepository;
    }

    public List<Kitchen> findAll() {
        System.out.println("Find all kitchens...");
        return kitchenRepository.findAll();
    }

    public Optional<Kitchen> findByName(String name) {
        return kitchenRepository.findByName(name);
    }

    public long count() {
        return kitchenRepository.count();
    }

    public List<Person> getDriver(Long id) {
        Optional<Kitchen> kitchen = kitchenRepository.findById(id);
        if (kitchen.isEmpty()) {
            LOGGER.log(Level.SEVERE,
                    "Kitchen is null");
            return new ArrayList<>();
        }
        List<Employee> employees = employeeRepository.findByKitchenId(kitchen.get().getId());
        if (!employees.isEmpty()) {
            LOGGER.log(Level.SEVERE,
                    "no Employees for Kitchen with id = " + kitchen.get().getId());
            return new ArrayList<>();
        }
        List<Person> driverList = new ArrayList<>();
        for (Employee e : employees) {
            Optional<Person> driver = personRepository.findByIdAndPersonTypeAndStatus(e.getId(), PersonType.DRIVER, Status.ACTIVE);
            if (!driver.isEmpty()) {
                driverList.add(driver.get());
            }
        }
        return driverList;
    }

    public List<Order> getActiveOrdersByDateAndKitchenId(Date date, Long kitchenId) {
        return orderRepository.findByDtAndKitchenIdAndStatus(date, kitchenId, Status.ACTIVE);
    }

    public void delete(Kitchen kitchen) {
        if (isNull(kitchen)) return;
        kitchen.setStatus(Status.INACTIVE);
        kitchenRepository.save(kitchen);
    }

    public void save(Kitchen kitchen) {
        if (isNull(kitchen)) return;
        kitchenRepository.save(kitchen);
    }

    private boolean isNull(Kitchen kitchen) {
        if (kitchen == null) {
            LOGGER.log(Level.SEVERE,
                    "Kitchen is null");
            return true;
        }
        return false;
    }
}