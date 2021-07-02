package com.rki.essenAufRaedern.backend.entity;

import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;


/**
 * The persistent class for the users database table.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date lastLogin;

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;

    @Email
    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    private Status status;

    @NotNull
    private String role;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLastLogin() {
        return this.lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        this.password = bcryptPasswordEncoder.encode(password);
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getUsername() {
        return this.username;
    }

    public static String[] getAllRoles() {
        return new String[]{"ADMINISTRATION", "KITCHEN", "DRIVER", "CLIENT", "CONTACT_PERSON", "LOCAL_COMMUNITY", "DEVELOPER"};
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public PersonType getPersonType() {
        return this.person.getPersonType();
    }

    public void setPersonType(PersonType personType) {
        this.person.setPersonType(personType);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        setRole();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return getStatus().equals(Status.ACTIVE);
    }

    public String getRole() {
        PersonType personType = getPersonType();
        if (personType.equals(PersonType.ADMINISTRATION)) {
            return "ADMINISTRATION";
        } else if (personType.equals(PersonType.KITCHEN)) {
            return "KITCHEN";
        } else if (personType.equals(PersonType.DRIVER)) {
            return "DRIVER";
        } else if (personType.equals(PersonType.CLIENT)) {
            return "CLIENT";
        } else if (personType.equals(PersonType.CONTACT_PERSON)) {
            return "CONTACT_PERSON";
        } else if (personType.equals(PersonType.LOCAL_COMMUNITY)) {
            return "LOCAL_COMMUNITY";
        } else if (personType.equals(PersonType.DEVELOPER)) {
            return "DEVELOPER";
        }
        return null;
    }

    public void setRole() {
        this.role = getRole();
    }
}