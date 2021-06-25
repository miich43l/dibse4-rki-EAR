package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.OrderInformation;
import com.rki.essenAufRaedern.backend.repository.OrderInformationRepository;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the order_information database table.
 */

@Service
public class OrderInformationService {
    private static final Logger LOGGER = Logger.getLogger(OrderInformationService.class.getName());
    private final OrderInformationRepository orderInformationRepository;

    public OrderInformationService(OrderInformationRepository orderInformationRepository) {
        this.orderInformationRepository = orderInformationRepository;
    }

    public List<OrderInformation> findAll() {
        return orderInformationRepository.findAll();
    }

    public long count() {
        return orderInformationRepository.count();
    }

    public void delete(OrderInformation orderInformation) {
        if (isNull(orderInformation)) return;
        orderInformation.setStatus(Status.INACTIVE);
        orderInformation.setDt_to(Calendar.getInstance().getTime());
        orderInformationRepository.save(orderInformation);
    }

    public void save(OrderInformation orderInformation) {
        if (isNull(orderInformation)) return;
        orderInformationRepository.save(orderInformation);
    }

    private boolean isNull(OrderInformation orderInformation) {
        if (orderInformation == null) {
            LOGGER.log(Level.SEVERE,
                    "Order information is null");
            return true;
        }
        return false;
    }
}