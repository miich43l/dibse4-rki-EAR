package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Order;
import com.vaadin.tutorial.crm.backend.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.NamedQuery;
import java.util.Date;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the orders database table.
 */

public interface OrderRepository extends JpaRepository<Order, Long> {

}