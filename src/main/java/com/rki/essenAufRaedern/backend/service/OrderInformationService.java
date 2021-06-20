package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.OrderInformation;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the order_information database table.
 */

@Service
public class OrderInformationService {
    private static final Logger LOGGER = Logger.getLogger(OrderInformationService.class.getName());
    private OrderInformation orderInformation;
}