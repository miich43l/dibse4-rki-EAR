package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.OrderInformation;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * The repository class for the order_information database table.
 */

public interface OrderInformationRepository extends JpaRepository<OrderInformation, Long> {

}