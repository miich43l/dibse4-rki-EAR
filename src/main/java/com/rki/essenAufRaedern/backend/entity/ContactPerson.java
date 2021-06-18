package com.rki.essenAufRaedern.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author arthurwaldner
 * The persistent class for the contact_person database table.
 */
@Entity
@Table(name = "contact_person")
@NamedQuery(name = "ContactPerson.findAll", query = "SELECT c FROM ContactPerson c")
public class ContactPerson implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_person_type_id")
    private ContactPersonType contactPersonType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_person_id")
    private Person contactPersonFrom;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    public ContactPerson() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContactPersonType getContactPersonType() {
        return this.contactPersonType;
    }

    public void setContactPersonType(ContactPersonType contactPersonType) {
        this.contactPersonType = contactPersonType;
    }

    public Person getContactPersonFrom() {
        return this.contactPersonFrom;
    }

    public void setContactPersonFrom(Person contactPersonFrom) {
        this.contactPersonFrom = contactPersonFrom;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}