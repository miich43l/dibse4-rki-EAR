package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Employee;
import com.rki.essenAufRaedern.backend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the employees database table.
 */

@Service
public class EmployeeService {
    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public List<Employee> findByKitchenId(Long id) { return employeeRepository.findByKitchenId(id);}

    public long count() {
        return employeeRepository.count();
    }

    public void delete(Employee employee) {
        employeeRepository.delete(employee);
    }

    public void save(Employee employee) {
        if (employee == null) {
            LOGGER.log(Level.SEVERE,
                    "Employee is null");
            return;
        }
        employeeRepository.save(employee);
    }


}