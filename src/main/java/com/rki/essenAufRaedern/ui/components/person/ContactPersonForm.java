package com.rki.essenAufRaedern.ui.components.person;

import com.rki.essenAufRaedern.backend.entity.ContactPerson;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.utility.ContactPersonType;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class ContactPersonForm extends FormLayout {

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    TextField phoneNumber = new TextField("Telefonnummer");
    ComboBox<ContactPersonType> contactPersonType = new ComboBox<>("Kontakttyp");

    Binder<Person> personBinder = new BeanValidationBinder<>(Person.class);
    private Person person;

    Binder<ContactPerson> contactPersonBinder = new BeanValidationBinder<>(ContactPerson.class);
    private ContactPerson contactPerson;

    public ContactPersonForm() {
        personBinder.bindInstanceFields(this);
        contactPersonBinder.bindInstanceFields(this);
        contactPersonType.setItems(ContactPersonType.values());

        add(firstName,
            lastName,
            phoneNumber,
            contactPersonType);
    }

    public boolean isValid() {
        return personBinder.isValid() && contactPersonBinder.isValid();
    }

    public void setContactPerson(ContactPerson contactPerson) {
        this.person = contactPerson.getContactPersonFrom();
        this.contactPerson = contactPerson;
        personBinder.readBean(person);
        contactPersonBinder.readBean(contactPerson);
    }

    public ContactPerson getContactPerson() {
        try {
            contactPersonBinder.writeBean(contactPerson);
            personBinder.writeBean(person);
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        return contactPerson;
    }
}
