package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.repository.AdditionalInformationRepository;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * The service class for the additional_information database table.
 */

@Service
public class AdditionalInformationService {
    private static final Logger LOGGER = Logger.getLogger(AdditionalInformationService.class.getName());
    private final AdditionalInformationRepository additionalInformationRepository;

    public AdditionalInformationService(AdditionalInformationRepository additionalInformationRepository) {
        this.additionalInformationRepository = additionalInformationRepository;
    }

    public List<AdditionalInformation> findAll() {
        return additionalInformationRepository.findAll();
    }

    public List<AdditionalInformation> findByPersonId(Long personId) {
        return additionalInformationRepository.findByPersonId(personId);
    }

    public long count() {
        return additionalInformationRepository.count();
    }

    public void delete(AdditionalInformation additionalInformation) {
        if (isNull(additionalInformation)) return;
        additionalInformation.setStatus(Status.INACTIVE);
        additionalInformationRepository.save(additionalInformation);
    }

    public void save(AdditionalInformation additionalInformation) {
        if (isNull(additionalInformation)) return;
        additionalInformationRepository.save(additionalInformation);
    }

    private boolean isNull(AdditionalInformation additionalInformation) {
        if (additionalInformation == null) {
            LOGGER.log(Level.SEVERE,
                    "Additional information is null");
            return true;
        }
        return false;
    }

    public List<AdditionalInformation> findForPersonAndType(Long personId, InformationType type) {
        return findByPersonId(personId).stream().filter(item -> item.getInformationType() == type).collect(Collectors.toList());
    }
}