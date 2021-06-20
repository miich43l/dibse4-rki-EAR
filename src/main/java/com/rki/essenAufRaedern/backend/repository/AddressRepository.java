package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the address database table.
 */

public interface AddressRepository extends JpaRepository<Address, Long> {
}