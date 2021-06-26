package com.rki.essenAufRaedern.backend.entity;

import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author arthurwaldner
 * The persistent class for the person database table.
 */
@Entity
public class Person{

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

    //Todo add @NotNull and @NotEmpty
    @Column(name = "phone_number")
    private String phoneNumber;
    @NotNull
    private Status status;

    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    private List<AdditionalInformation> additionalInformation = new ArrayList<>();

    @OneToMany(mappedBy = "contactPersonFrom", fetch = FetchType.EAGER)
    private Set<ContactPerson> contactPersonFrom = new HashSet<>();

    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    private Set<ContactPerson> contactPersons = new HashSet<>();

    @OneToMany(mappedBy = "person")
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "person", fetch = FetchType.EAGER)
    private Set<OrderInformation> orderInformation = new HashSet<>();

    @OneToMany(mappedBy = "person")
    private List<Order> orders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;

    @NotNull
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

    public String getFullName() {
        return getFirstName() + " " + getLastName();
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

    public List<AdditionalInformation> getAdditionalInformation(InformationType ... type) {
        if(type.length > 0) {
            return additionalInformation.stream()
                                        .filter(item -> Arrays.stream(type).anyMatch(item_ -> item.getInformationType() == item_))
                                        .collect(Collectors.toList());
        }

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

    public Set<ContactPerson> getContactPersonFrom() {
        return this.contactPersonFrom;
    }

    public void setContactPersonFrom(Set<ContactPerson> contactPersonFrom) {
        this.contactPersonFrom = contactPersonFrom;
    }

    public ContactPerson addContactPersonFrom(ContactPerson contactPersonFrom) {
        this.contactPersonFrom.add(contactPersonFrom);
        contactPersonFrom.setContactPersonFrom(this);

        return contactPersonFrom;
    }

    public ContactPerson removeContactPersonFrom(ContactPerson contactPersonFrom) {
        getContactPersonFrom().remove(contactPersonFrom);
        contactPersonFrom.setContactPersonFrom(null);

        return contactPersonFrom;
    }

    public Set<ContactPerson> getContactPersons() {
        return this.contactPersons;
    }

    public void setContactPerson(Set<ContactPerson> Person) {
        this.contactPersons = Person;
    }

    public void addContactPerson(ContactPerson contactPerson) {
        this.contactPersons.add(contactPerson);
    }

    public void removeContactPerson(ContactPerson contactPerson) {
        this.contactPersons.remove(contactPerson);
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

    public Set<OrderInformation> getOrderInformation() {
        return this.orderInformation;
    }

    public void setOrderInformation(Set<OrderInformation> orderInformation) {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", birthdate=" + birthdate +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status=" + status +
                ", address=" + address +
                ", personType=" + personType +
                '}';
    }
}