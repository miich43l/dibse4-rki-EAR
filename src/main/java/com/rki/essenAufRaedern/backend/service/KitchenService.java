package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.repository.*;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


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
    private final UserService userService;


    public KitchenService(KitchenRepository kitchenRepository, PersonRepository personRepository, EmployeeRepository employeeRepository, OrderRepository orderRepository, UserService userService) {
        this.kitchenRepository = kitchenRepository;
        this.personRepository = personRepository;
        this.employeeRepository = employeeRepository;
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    public List<Kitchen> findAll() {
        System.out.println("Find all kitchens...");
        return kitchenRepository.findAll();
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
        List<Employee> employees = employeeRepository.findByKitchenId(id);
        if (employees.isEmpty()) {
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

    public Kitchen getKitchenForLoggedInEmployee() {
        User currentUser = userService.getCurrentUser();
        Person currentPerson = currentUser.getPerson();
        Employee currentEmployee = currentPerson.getEmployee();
        if(currentEmployee == null) {
            return null;
        }

        return currentEmployee.getKitchen();
    }

    public List<Order> getOpenOrdersForKitchen(Kitchen kitchen, Date date) {
        Predicate<Order> orderPredicate = order -> {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date);

            Calendar c2 = Calendar.getInstance();
            c2.setTime(order.getDt());

            return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                    && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                    && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
                    && (order.getDelivered() == null
                    && order.getNotDeliverable() == null
                    && order.getStatus() == Status.ACTIVE);
        };

        return kitchen.getOrders().stream().filter(orderPredicate).collect(Collectors.toList());
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