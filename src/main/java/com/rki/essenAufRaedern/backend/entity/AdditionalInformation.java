package com.rki.essenAufRaedern.backend.entity;

import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.backend.utility.Status;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * @author arthurwaldner
 * The persistent class for the additional_information database table.
 */
@Entity
@Table(name = "additional_information")
public class AdditionalInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotEmpty
    private String value;

    @NotNull
    @Column(name = "information_type")
    private InformationType informationType;

    @NotNull
    private Status status;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    public AdditionalInformation() {
        this.status = Status.ACTIVE;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public InformationType getInformationType() {
        return this.informationType;
    }

    public void setInformationType(InformationType informationType) {
        this.informationType = informationType;
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}