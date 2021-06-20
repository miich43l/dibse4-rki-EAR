package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Employee;
import com.rki.essenAufRaedern.backend.entity.Kitchen;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.repository.AddressRepository;
import com.rki.essenAufRaedern.backend.repository.EmployeeRepository;
import com.rki.essenAufRaedern.backend.repository.KitchenRepository;
import com.rki.essenAufRaedern.backend.repository.PersonRepository;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the kitchen database table.
 */

@Service
public class KitchenService {
    private static final Logger LOGGER = Logger.getLogger(KitchenService.class.getName());
    private final KitchenRepository kitchenRepository;
    private final AddressRepository addressRepository;
    private final PersonRepository personRepository;
    private final EmployeeRepository employeeRepository;


    public KitchenService(KitchenRepository kitchenRepository, AddressRepository addressRepository, PersonRepository personRepository, EmployeeRepository employeeRepository) {
        this.kitchenRepository = kitchenRepository;
        this.addressRepository = addressRepository;
        this.personRepository = personRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
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
            return null;
        }
        List<Employee> employees = employeeRepository.findByKitchenId(kitchen.get().getId());
        if (!employees.isEmpty()) {
            LOGGER.log(Level.SEVERE,
                    "no Employees for Kitchen with id = " + kitchen.get().getId());
            return null;
        }
        List<Person> driverList = new ArrayList<>();
        for (Employee e : employees) {
            Optional<Person> driver = personRepository.findByIdAndPersonTypeAndStatus(e.getId(), PersonType.Driver, Status.Active);
            if (!driver.isEmpty()) {
                driverList.add(driver.get());
            }
        }

        return driverList;
    }

    public void delete(Kitchen kitchen) {
        kitchen.setStatus(Status.Inactive);
        kitchenRepository.save(kitchen);
    }

    public void save(Kitchen kitchen) {
        if (kitchen == null) {
            LOGGER.log(Level.SEVERE,
                    "Kitchen is null");
            return;
        }
        kitchenRepository.save(kitchen);
    }
}