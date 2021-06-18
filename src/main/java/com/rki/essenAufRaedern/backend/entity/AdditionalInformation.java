package com.rki.essenAufRaedern.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author arthurwaldner
 * The persistent class for the additional_information database table.
 */
@Entity
@Table(name = "additional_information")
@NamedQuery(name = "AdditionalInformation.findAll", query = "SELECT a FROM AdditionalInformation a")
public class AdditionalInformation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotEmpty
    private String value;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "information_type_id")
    private InformationType informationType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    public AdditionalInformation() {
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

}