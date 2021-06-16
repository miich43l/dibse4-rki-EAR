package com.rki.essenAufReaedern.backend.service;

import com.rki.essenAufReaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufReaedern.backend.repository.AdditionalInformationRepository;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the additional_information database table.
 */
@Service
public class AdditionalInformationService {
    private static final Logger LOGGER = Logger.getLogger(AdditionalInformationService.class.getName());
    private AdditionalInformationRepository additionalInformationRepository;

    public List<AdditionalInformation> findAll() {
        return additionalInformationRepository.findAll();
    }
    public List<AdditionalInformation> findByPersonId(Long personId) {
        return additionalInformationRepository.findByPersonId(personId);
    }

}