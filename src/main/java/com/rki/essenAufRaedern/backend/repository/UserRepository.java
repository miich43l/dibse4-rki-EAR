package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * The repository class for the users database table.
 */

public interface UserRepository extends JpaRepository<User, Long> {
    
    User findByUsername(String username);

    User findByUsernameIgnoreCase(String username);
}