package com.rki.essenAufRaedern.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author arthurwaldner
 * The persistent class for the information_type database table.
 */
@Entity
@Table(name = "information_type")
public class InformationType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String description;


    public InformationType() {
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
}