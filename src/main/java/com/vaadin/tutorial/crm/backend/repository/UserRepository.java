package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Person;
import com.vaadin.tutorial.crm.backend.entity.User;
import com.vaadin.tutorial.crm.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the users database table.
 * @findUserByUserName get user by username
 * @findUsersByStatus get user's by status
 */


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u where lower(u.username) like lower(concat('%', trim(:username), '%'))")
    List<Person> findUserByUserName(@Param("username") String username);

    @Query("SELECT u FROM User u where u.status = :status")
    List<Person> findContactPersonsForPersonId(@Param("status") Status status);

}