package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.Kitchen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * @author arthurwaldner
 * The repository class for the kitchen database table.
 */

public interface KitchenRepository extends JpaRepository<Kitchen, Long> {

    Optional<Kitchen> findByName(String name);
}
