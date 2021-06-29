package com.rki.essenAufRaedern.backend.service;

import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.entity.User;
import com.rki.essenAufRaedern.backend.repository.PersonRepository;
import com.rki.essenAufRaedern.backend.repository.UserRepository;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    public String createUserNameFromPerson(Person person) {
        return person.getFirstName() + "_" + person.getLastName();
    }

    public User addUserForPerson(Person person) {
        User user = new User();
        user.setUsername(createUserNameFromPerson(person));
        user.setEmail(createEmailAddressFromPerson(person));
        user.setStatus(Status.ACTIVE);
        user.setPerson(person);
        user.setPassword("changeMe");
        userRepository.save(user);

        return user;
    }

    private String createEmailAddressFromPerson(Person person) {
        return person.getFirstName() + "." + person.getLastName() + "@rki.at";
    }

    public void addUser(String firstName, String LastName, String phoneNumber, String email, PersonType personType, String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setStatus(Status.ACTIVE);
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(LastName);
        person.setPhoneNumber(phoneNumber);
        person.setPersonType(personType);
        person.setStatus(Status.ACTIVE);
        personRepository.save(person);
        user.setPerson(person);
        user.setPassword(password);
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
        return userRepository.getByUsername(username
        );
    }

    public User getUserByUsernameIgnoreCase(String username) {
        return userRepository.findByUsernameIgnoreCase(username
        );
    }

    public User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserByUsername(userDetails.getUsername());
    }
}