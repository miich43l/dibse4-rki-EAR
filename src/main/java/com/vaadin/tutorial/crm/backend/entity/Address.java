package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author arthurwaldner
 * The persistent class for the address database table.
 */
@Entity
@NamedQuery(name = "Address.findAll", query = "SELECT a FROM Address a")
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotEmpty
    private String city;

    @NotNull
    @NotEmpty
    private Integer floor;

    @Column(name = "house_number")
    private Integer houseNumber;

    @NotNull
    @NotEmpty
    private String state;

    @NotNull
    @NotEmpty
    private String street;

    @NotNull
    @NotEmpty
    @Column(name = "zip_code")
    private String zipCode;

    public Address() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getFloor() {
        return this.floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getHouseNumber() {
        return this.houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = this.zipCode;
    }

    @Override
    public String toString() {
        return getZipCode()
                + " " + getCity()
                + " " + getStreet()
                + " " + getHouseNumber();
    }
}