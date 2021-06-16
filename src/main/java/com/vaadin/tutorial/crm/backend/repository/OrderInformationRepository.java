package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.OrderInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repositiry class for the order_information database table.
 * @findAll get all order_information's
 * @findOrderInformationByPersonId get order_information by person_id
 */

@NamedQuery(name = "OrderInformation.findAll", query = "SELECT * FROM order_information")
@NamedQuery(name = "OrderInformation.findOrderInformationByPersonId", query = "SELECT * FROM order_information where person_id = ?")
public interface OrderInformationRepository extends JpaRepository<OrderInformation, Long> {

    List<OrderInformation> findAll();

    List<OrderInformation> findOrderInformationByPersonId(Long personId);
}