package com.rki.essenAufRaedern.backend.entity;

import com.rki.essenAufRaedern.backend.utility.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * The persistent class for the order_information database table.
 */
@Entity
@Table(name = "order_information")
public class OrderInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Status monday;

    @NotNull
    private Status tuesday;

    @NotNull
    private Status wednesday;

    @NotNull
    private Status thursday;

    @NotNull
    private Status saturday;

    @NotNull
    private Status sunday;

    @NotNull
    private Status friday;

    @NotNull
    private Date dt_form;

    private Date dt_to;

    @NotNull
    private Status status;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    public OrderInformation() {
    }

    public Status getMonday() {
        return monday;
    }

    public void setMonday(Status monday) {
        this.monday = monday;
    }

    public Status getTuesday() {
        return tuesday;
    }

    public void setTuesday(Status tuesday) {
        this.tuesday = tuesday;
    }

    public Status getWednesday() {
        return wednesday;
    }

    public void setWednesday(Status wednesday) {
        this.wednesday = wednesday;
    }

    public Status getThursday() {
        return thursday;
    }

    public void setThursday(Status thursday) {
        this.thursday = thursday;
    }

    public Status getSaturday() {
        return saturday;
    }

    public void setSaturday(Status saturday) {
        this.saturday = saturday;
    }

    public Status getSunday() {
        return sunday;
    }

    public void setSunday(Status sunday) {
        this.sunday = sunday;
    }

    public Status getFriday() {
        return friday;
    }

    public void setFriday(Status friday) {
        this.friday = friday;
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getDt_form() {
        return dt_form;
    }

    public void setDt_form(Date dt_form) {
        this.dt_form = dt_form;
    }

    public Date getDt_to() {
        return dt_to;
    }

    public void setDt_to(Date dt_to) {
        this.dt_to = dt_to;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}