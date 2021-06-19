package com.rki.essenAufRaedern.backend.repository;

import com.rki.essenAufRaedern.backend.entity.User;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the users database table.
 */

public interface UserRepository extends JpaRepository<User, Long> {

}