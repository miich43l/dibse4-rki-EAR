package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.entity.InformationType;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the additional_information database table.
 */

public interface AdditionalInformationRepository extends JpaRepository<AdditionalInformation, Long> {

}