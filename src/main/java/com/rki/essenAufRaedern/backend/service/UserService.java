package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.User;
import com.rki.essenAufRaedern.backend.repository.PersonRepository;
import com.rki.essenAufRaedern.backend.repository.UserRepository;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The service class for the users database table.
 */

@Service
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private UserRepository userRepository;
    private PersonRepository personRepository;

    public UserService(UserRepository userRepository, PersonRepository personRepository) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public long count() {
        return userRepository.count();
    }

    public void delete(User user) {
        if (isNull(user)) return;
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
    }

    public void save(User user) {
        if (isNull(user)) return;
        userRepository.save(user);
    }

    private boolean isNull(User user) {
        if (user == null) {
            LOGGER.log(Level.SEVERE,
                    "User is null");
            return true;
        }
        return false;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByUsernameIgnoreCase(String username) {
        return userRepository.findByUsernameIgnoreCase(username
        );
    }

    public User getCurrentUser() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return getUserByUsernameIgnoreCase(userDetails.getUsername());
        } catch (Exception e) {
            return null;
        }
    }
}