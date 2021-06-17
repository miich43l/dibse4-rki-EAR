package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author arthurwaldner
 * The repository class for the employees database table.
 */

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}