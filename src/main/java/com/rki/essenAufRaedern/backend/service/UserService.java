package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.User;
import com.rki.essenAufRaedern.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author arthurwaldner
 * The service class for the users database table.
 */

@Service
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public long count() {
        return userRepository.count();
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public void save(User user) {
        if (user == null) {
            LOGGER.log(Level.SEVERE,
                    "User is null");
            return;
        }
        userRepository.save(user);
    }
}