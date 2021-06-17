package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Kitchen;
import com.vaadin.tutorial.crm.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the kitchen database table.
 * @findAll get all kitchens
 * @findKitchenByName get kitchen ba name
 * @findKitchensByStatus get kitchens by status (ENUM)
 * @findKitchenByAddressId get kitchen by address_id
 */

@NamedQuery(name = "Kitchen.findAll", query = "SELECT * FROM kitchen")
@NamedQuery(name = "Kitchen.findKitchenByName", query = "SELECT * FROM kitchen where lower(name) like lower(concat('%', trim(?), '%'))")
@NamedQuery(name = "Kitchen.findKitchensByStatus", query = "SELECT * FROM kitchen where status = ?")
@NamedQuery(name = "Kitchen.findKitchenByAddressId", query = "SELECT * FROM kitchen where address_id = ?")
public interface KitchenRepository extends JpaRepository<Kitchen, Long> {

    List<Kitchen> findAll();

    List<Kitchen> findKitchenByName(String name);

    List<Kitchen> findAllKitchensByStatus(Status status);

    List<Kitchen> findKitchenByAddressId(Long kitchenId);
}
