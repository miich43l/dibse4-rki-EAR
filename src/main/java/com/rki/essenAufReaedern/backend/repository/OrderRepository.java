package com.rki.essenAufReaedern.backend.repository;

import com.rki.essenAufReaedern.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.Date;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the orders database table.
 * @findAll get all order's
 * @findOrdersByPersonId get all order's by person_id
 * @findOrdersByPersonIdAndDate get all order's by person_id and date
 * @findOrdersByKitchenId get all order's by kitchen_id
 * @findOrdersByKitchenIdAndDate get all order's by kitchen_id and date
 */
@NamedQuery(name = "Order.findAll", query = "SELECT * FROM orders")
@NamedQuery(name = "Order.findOrdersByPersonId", query = "SELECT * FROM orders where person_id = ?")
@NamedQuery(name = "Order.findOrdersByPersonIdAndDate", query = "SELECT * FROM orders where person_id = ? and dt::date = ?::date")
@NamedQuery(name = "Order.findOrdersByKitchenId", query = "SELECT * FROM orders where kitchen_id = ?")
@NamedQuery(name = "Order.findOrdersByKitchenIdAndDate", query = "SELECT * FROM orders where kitchen_id = ? and dt::date = ?::date")
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAll();

    List<Order> findOrdersByPersonId(Long personId);

    List<Order> findOrdersByPersonIdAndDate(Long personId, Date dtFrom);

    List<Order> findOrdersByKitchenId(Long kitchenId);

    List<Order> findOrdersByKitchenIdAndDate(Long kitchenId, Date dtFrom);

}