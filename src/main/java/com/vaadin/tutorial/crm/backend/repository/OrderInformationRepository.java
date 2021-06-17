package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.OrderInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repositiry class for the order_information database table.
 */

public interface OrderInformationRepository extends JpaRepository<OrderInformation, Long> {

}