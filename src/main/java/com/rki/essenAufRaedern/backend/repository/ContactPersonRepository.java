package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.ContactPerson;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * The repository class for the address database table.
 */

public interface ContactPersonRepository extends JpaRepository<ContactPerson, Long> {

    List<ContactPerson> findByContactPersonFromId(Long personId);
}