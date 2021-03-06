package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * The repository class for the person database table.
 */

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findById(Long id);

    Set<Person> findByFirstNameContainingOrLastNameContaining(String firstname, String lastname);

    List<Person> findByPersonTypeAndStatus(PersonType personType, Status status);

    Optional<Person> findByIdAndPersonTypeAndStatus(Long id, PersonType personType, Status status);

    @Query("select p from Person p " +
            "where p.personType = :type " +
            "and p.status = :active " +
            "and ((lower(p.firstName) like lower(concat('%', :firstname, '%')) or lower(p.lastName) like lower(concat('%', :lastname, '%'))) " +
            "\t or (lower(p.firstName) like lower(concat('%', :lastname, '%')) or lower(p.lastName) like lower(concat('%', :firstname, '%'))))")
    List<Person> findDistinctByPersonTypeAndStatusAndFirstNameAndLastName(@Param("type") PersonType client, @Param("active") Status active, @Param("firstname") String firstname, @Param("lastname") String lastname);

    @Query("select p from Person p " +
            "where p.personType = :type " +
            "and p.status = :active " +
            "and ((lower(p.firstName) like lower(concat('%', :searchTerm, '%'))) " +
            "or (lower(p.lastName) like lower(concat('%', :searchTerm, '%'))))")
    List<Person> findByFieldInputPersonTypeAndStatus(@Param("searchTerm") String searchTerm, @Param("type") PersonType client, @Param("active") Status active);

    Set<Person> findByFirstNameContainingAndLastNameContaining(String firstname, String lastname);
}
