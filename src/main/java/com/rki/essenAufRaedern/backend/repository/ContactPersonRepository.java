package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.ContactPerson;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author arthurwaldner
 * The repository class for the address database table.
 */

public interface ContactPersonRepository extends JpaRepository<ContactPerson, Long> {
}