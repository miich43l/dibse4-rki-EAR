package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @author arthurwaldner
 * The repository class for the person database table.
 */

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findById(Long id);

    Optional<Person> findByFirstNameContainingOrLastNameContaining(String firstname, String lastname);

    List<Person> findByPersonTypeAndStatus(PersonType personType, Status status);

    Optional<Person> findByIdAndPersonTypeAndStatus(Long id, PersonType personType, Status status);

    @Query( "select p from Person p " +
                    "where p.personType = :type " +
                    "and p.status = :active " +
                    "and ((lower(p.firstName) like lower(concat('%', :firstname, '%')) or lower(p.lastName) like lower(concat('%', :lastname, '%'))) " +
                    "\t or (lower(p.firstName) like lower(concat('%', :lastname, '%')) or lower(p.lastName) like lower(concat('%', :firstname, '%'))))")
    List<Person> findDistinctByPersonTypeAndStatusAndFirstNameAndLastName(@Param("type") PersonType client, @Param("active") Status active, @Param("firstname") String firstname, @Param("lastname") String lastname);
}