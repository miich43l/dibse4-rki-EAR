package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the employees database table.
 */

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByKitchenId(Long id);
}