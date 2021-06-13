package com.rki.essenAufReaedern.backend.repository;

import com.rki.essenAufReaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufReaedern.backend.entity.InformationType;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the additional_information database table.
 * @findAll get all records
 * @findByPersonId get records by person_id
 * @getAllAdditionalInformationTypes get all types
 */

@NamedQuery(name = "AdditionalInformation.findAll", query = "SELECT * FROM additional_information")
@NamedQuery(name = "AdditionalInformation.findByPersonId", query = "SELECT * FROM additional_information where person_id = ?")
@NamedQuery(name = "AdditionalInformation.getAllAdditionalInformationTypes", query = "SELECT * FROM additional_information")
public interface AdditionalInformationRepository extends JpaRepository<AdditionalInformation, Long> {

    List<AdditionalInformation> findAll();

    List<AdditionalInformation> findByPersonId(Long person_id);

    List<InformationType> getAllAdditionalInformationTypes();
}