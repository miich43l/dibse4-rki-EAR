package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.AdditionalInformation;
import com.vaadin.tutorial.crm.backend.entity.InformationType;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the additional_information database table.
 */

@NamedQuery(name = "AdditionalInformation.findAll", query = "SELECT * FROM additional_information")
@NamedQuery(name = "AdditionalInformation.findByPersonId", query = "SELECT * FROM additional_information where person_id = ?")
public interface AdditionalInformationRepository extends JpaRepository<AdditionalInformation, Long> {

}