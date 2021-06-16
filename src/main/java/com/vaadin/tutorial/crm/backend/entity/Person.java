package com.vaadin.tutorial.crm.backend.entity;

import com.vaadin.tutorial.crm.backend.utility.Status;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author arthurwaldner
 * The persistent class for the person database table.
 */
@Entity
@NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p")
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date birthdate;

    @NotNull
    @NotEmpty
    @Column(name = "first_name")
    private String firstName = "";

    @NotNull
    @NotEmpty
    @Column(name = "last_name")
    private String lastName = "";

    @NotNull
    private Status status;

    @OneToMany(mappedBy = "person")
    private List<AdditionalInformation> additionalInformation = new ArrayList<>();

    @OneToMany(mappedBy = "contactPersonFrom")
    private List<ContactPerson> contactPersonFrom = new ArrayList<>();

    @OneToMany(mappedBy = "person")
    private List<ContactPerson> Person = new ArrayList<>();

    @OneToMany(mappedBy = "person")
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "person")
    private List<OrderInformation> orderInformation = new ArrayList<>();

    @OneToMany(mappedBy = "person")
    private List<Order> orders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_type_id")
    private PersonType personType;

    @OneToMany(mappedBy = "person")
    private List<User> users = new ArrayList<>();

    public Person() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<AdditionalInformation> getAdditionalInformation() {
        return this.additionalInformation;
    }

    public void setAdditionalInformation(List<AdditionalInformation> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public AdditionalInformation addAdditionalInformation(AdditionalInformation additionalInformation) {
        getAdditionalInformation().add(additionalInformation);
        additionalInformation.setPerson(this);

        return additionalInformation;
    }

    public AdditionalInformation removeAdditionalInformation(AdditionalInformation additionalInformation) {
        getAdditionalInformation().remove(additionalInformation);
        additionalInformation.setPerson(null);

        return additionalInformation;
    }

    public List<ContactPerson> getContactPersonFrom() {
        return this.contactPersonFrom;
    }

    public void setContactPersonFrom(List<ContactPerson> contactPersonFrom) {
        this.contactPersonFrom = contactPersonFrom;
    }

    public ContactPerson addContactPersonFrom(ContactPerson contactPersonFrom) {
        getContactPersonFrom().add(contactPersonFrom);
        contactPersonFrom.setContactPersonFrom(this);

        return contactPersonFrom;
    }

    public ContactPerson removeContactPersonFrom(ContactPerson contactPersonFrom) {
        getContactPersonFrom().remove(contactPersonFrom);
        contactPersonFrom.setContactPersonFrom(null);

        return contactPersonFrom;
    }

    public List<ContactPerson> getPerson() {
        return this.Person;
    }

    public void setPerson(List<ContactPerson> Person) {
        this.Person = Person;
    }

    public List<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Employee addEmployee(Employee employee) {
        getEmployees().add(employee);
        employee.setPerson(this);

        return employee;
    }

    public Employee removeEmployee(Employee employee) {
        getEmployees().remove(employee);
        employee.setPerson(null);

        return employee;
    }

    public List<OrderInformation> getOrderInformation() {
        return this.orderInformation;
    }

    public void setOrderInformation(List<OrderInformation> orderInformation) {
        this.orderInformation = orderInformation;
    }

    public OrderInformation addOrderInformation(OrderInformation orderInformation) {
        getOrderInformation().add(orderInformation);
        orderInformation.setPerson(this);

        return orderInformation;
    }

    public OrderInformation removeOrderInformation(OrderInformation orderInformation) {
        getOrderInformation().remove(orderInformation);
        orderInformation.setPerson(null);

        return orderInformation;
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Order addOrder(Order order) {
        getOrders().add(order);
        order.setPerson(this);

        return order;
    }

    public Order removeOrder(Order order) {
        getOrders().remove(order);
        order.setPerson(null);

        return order;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public PersonType getPersonType() {
        return this.personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User addUser(User user) {
        getUsers().add(user);
        user.setPerson(this);

        return user;
    }

    public User removeUser(User user) {
        getUsers().remove(user);
        user.setPerson(null);

        return user;
    }

}