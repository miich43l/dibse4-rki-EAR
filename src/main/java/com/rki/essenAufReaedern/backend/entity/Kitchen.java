package com.rki.essenAufReaedern.backend.entity;

import com.rki.essenAufReaedern.backend.utility.Status;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;


/**
 * @author arthurwaldner
 * The persistent class for the kitchen database table.
 */
@Entity
@NamedQuery(name = "Kitchen.findAll", query = "SELECT k FROM Kitchen k")
public class Kitchen implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Status status;

    @OneToMany(mappedBy = "kitchen")
    private Set<Employee> employees;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    @OneToMany(mappedBy = "kitchen")
    private Set<Order> orders;

    public Kitchen() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    public Employee addEmployee(Employee employee) {
        getEmployees().add(employee);
        employee.setKitchen(this);

        return employee;
    }

    public Employee removeEmployee(Employee employee) {
        getEmployees().remove(employee);
        employee.setKitchen(null);

        return employee;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Order addOrder(Order order) {
        getOrders().add(order);
        order.setKitchen(this);

        return order;
    }

    public Order removeOrder(Order order) {
        getOrders().remove(order);
        order.setKitchen(null);

        return order;
    }

}