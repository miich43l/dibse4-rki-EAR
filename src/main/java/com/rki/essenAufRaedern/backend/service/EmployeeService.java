package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Employee;
import com.rki.essenAufRaedern.backend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the employees database table.
 */

@Service
public class EmployeeService {
    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
    private EmployeeRepository employeeRepository;
}