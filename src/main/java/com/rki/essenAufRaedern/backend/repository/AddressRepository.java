package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * The repository class for the address database table.
 */

public interface AddressRepository extends JpaRepository<Address, Long> {
}