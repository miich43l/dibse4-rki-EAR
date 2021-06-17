package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Kitchen;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author arthurwaldner
 * The repository class for the kitchen database table.
 */

public interface KitchenRepository extends JpaRepository<Kitchen, Long> {

}
