package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author arthurwaldner
 * The repository class for the users database table.
 */


public interface UserRepository extends JpaRepository<User, Long> {

}