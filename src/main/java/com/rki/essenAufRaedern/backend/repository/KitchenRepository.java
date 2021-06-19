package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Kitchen;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the kitchen database table.
 */

public interface KitchenRepository extends JpaRepository<Kitchen, Long> {

}
