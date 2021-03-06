package com.rki.essenAufRaedern.backend.entity;

import com.rki.essenAufRaedern.backend.utility.ContactPersonType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the contact_person database table.
 */
@Entity
@Table(name = "contact_person")
public class ContactPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "contact_person_type")
    private ContactPersonType contactPersonType;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_person_id")
    private Person contactPersonFrom;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
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