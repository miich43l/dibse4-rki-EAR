package com.rki.essenAufRaedern.backend.entity;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author arthurwaldner
 * The persistent class for the address database table.
 */
@Entity
public class Address{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotEmpty
    private String city;

    @NotNull
    @NotEmpty
    private String floor;

    @Column(name = "house_number")
    private String houseNumber;

    @NotNull
    @NotEmpty
    private String country;

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

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getHouseNumber() {
        return this.houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getState() {
        return this.country;
    }

    public void setState(String country) {
        this.country = country;
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
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return getZipCode()
                + " " + getCity()
                + " " + getStreet()
                + " " + getHouseNumber();
    }

}