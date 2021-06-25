package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the orders database table.
 */

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByDtAndKitchenIdAndStatus(Date date, Long kitchenId, Status active);
}