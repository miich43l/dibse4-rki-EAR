package com.rki.essenAufReaedern.backend.repository;

import com.rki.essenAufReaedern.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the address database table.
 * @findAll get all addresses
 */

@NamedQuery(name = "Address.findAll", query = "SELECT * FROM address")
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAll();
}