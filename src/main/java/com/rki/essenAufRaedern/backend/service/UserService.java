package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.User;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the users database table.
 */

@Service
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private UserService userService;
}