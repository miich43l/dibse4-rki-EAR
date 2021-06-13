package com.rki.essenAufReaedern.backend.repository;

import com.rki.essenAufReaedern.backend.entity.User;
import com.rki.essenAufReaedern.backend.utility.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.NamedQuery;
import java.util.List;


/**
 * @author arthurwaldner
 * The repository class for the users database table.
 * @findAll get all user's
 * @findUserByUserName get user by username
 * @findUsersByStatus get user's by status
 */


@NamedQuery(name = "User.findAll", query = "SELECT * FROM user")
@NamedQuery(name = "User.findUserByUserName", query = "SELECT * FROM user where lower(username) like lower(concat('%', trim(?), '%'))")
@NamedQuery(name = "User.findUsersByStatus", query = "SELECT * FROM user where status = ?")
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAll();

    List<User> findUserByUserName(String username);

    List<User> findUsersByStatus(Status status);

}