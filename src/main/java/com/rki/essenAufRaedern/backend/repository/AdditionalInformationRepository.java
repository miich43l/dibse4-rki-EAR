package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * The repository class for the additional_information database table.
 */

public interface AdditionalInformationRepository extends JpaRepository<AdditionalInformation, Long> {

    List<AdditionalInformation> findByPersonId(Long personId);
}