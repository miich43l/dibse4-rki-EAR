package com.rki.essenAufReaedern.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;


/**
 * @author arthurwaldner
 * The persistent class for the person_type database table.
 */
@Entity
@Table(name = "person_type")
@NamedQuery(name = "PersonType.findAll", query = "SELECT p FROM PersonType p")
public class PersonType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    @OneToMany(mappedBy = "personType")
    private Set<Person> persons;

    public PersonType() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Person> getPersons() {
        return this.persons;
    }

    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }

    public Person addPerson(Person person) {
        getPersons().add(person);
        person.setPersonType(this);

        return person;
    }

    public Person removePerson(Person person) {
        getPersons().remove(person);
        person.setPersonType(null);

        return person;
    }

}