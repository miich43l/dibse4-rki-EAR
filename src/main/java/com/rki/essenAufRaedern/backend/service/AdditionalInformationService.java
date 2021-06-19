package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.repository.AdditionalInformationRepository;
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

}