package com.rki.essenAufRaedern.backend.entity;

import com.rki.essenAufRaedern.backend.utility.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the employees database table.
 */
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Status status;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Kitchen kitchen;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    private Person person;

    public Employee() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Kitchen getKitchen() {
        return this.kitchen;
    }

    public void setKitchen(Kitchen kitchen) {
        this.kitchen = kitchen;
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
        person.setEmployee(this);
    }

}