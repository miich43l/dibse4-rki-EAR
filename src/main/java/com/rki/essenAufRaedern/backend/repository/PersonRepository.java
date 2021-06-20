package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}