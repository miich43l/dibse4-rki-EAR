package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the person database table.
 * @findAll get all persons
 * @findPersonByName get person by first or last name
 * @findContactPersonsForPersonId get contact person's of person
 */
@NamedQuery(name = "Order.findAll", query = "SELECT * FROM person")
@NamedQuery(name = "Order.findPersonByName", query = "SELECT * FROM person" +
        "where lower(first_name) like lower(concat('%', trim(?), '%'))" +
        "or lower(last_name) like lower(concat('%', trim(?), '%')))")
@NamedQuery(name = "Order.findPersonByName", query = "SELECT * FROM person" +
        "where lower(concat(first_name, ' ', last_name) like lower(concat('%', trim(?), '%'))" +
        "or lower(concat(last_name, ' ', first_name) like lower(concat('%', trim(?), '%'))")
@NamedQuery(name = "Order.findContactPersonsForPersonId", query = "SELECT cp.* FROM person p" +
        "join contact_person c ON c.contact_person_id = p.id" +
        "join person cp ON cp.id = c.person_id" +
        "where p.id = ")
public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findAll();

    List<Person> findPersonByName(String name);

    List<Person> findPersonByFullName(String fullName);

    List<Person> findContactPersonsForPersonId(Long personId);

}