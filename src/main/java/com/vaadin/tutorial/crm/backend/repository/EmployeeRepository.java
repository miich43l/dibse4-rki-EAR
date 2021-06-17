package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Employee;
import com.vaadin.tutorial.crm.backend.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the employees database table.
 * @findAll get all employees
 * @findEmployeeByPersonId get employees by person_id
 * @findEmployeesByStatus get employees by status (ENUM)
 * @findEmployeeByKitchenId get employees by kitchen_id
 * @findEmployeesByKitchenIdAndStatus get employees by kitchen_id and status
 */

@NamedQuery(name = "Employee.findAll", query = "SELECT * FROM employee")
@NamedQuery(name = "Employee.findEmployeeByPersonId", query = "SELECT * FROM employee where person_id = ?")
@NamedQuery(name = "Employee.findEmployeesByStatus", query = "SELECT * FROM employee where status = ?")
@NamedQuery(name = "Employee.findEmployeesByKitchenId", query = "SELECT * FROM employee where kitchen_id = ?")
@NamedQuery(name = "Employee.findEmployeesByKitchenIdAndStatus", query = "SELECT * FROM employee where kitchen_id = ? and status = ?")

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAll();

    List<Employee> findEmployeeByPersonId(Long Person_id);

    List<Employee> findEmployeesByStatus(Status status);

    List<Employee> findEmployeeByKitchenId(Long Kitchen_id);

    List<Employee> findEmployeesByKitchenIdAndStatus(Long Kitchen_id, Status status);
}