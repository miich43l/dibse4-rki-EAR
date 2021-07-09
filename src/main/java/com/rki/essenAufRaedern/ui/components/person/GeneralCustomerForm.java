package com.rki.essenAufRaedern.ui.components.person;

import com.rki.essenAufRaedern.backend.entity.Person;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;

public class GeneralCustomerForm extends FormLayout {

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");

    DatePicker birthdate = new DatePicker();
    TextField phoneNumber = new TextField("Telefonnummer");

    Binder<Person> personBinder = new BeanValidationBinder<>(Person.class);
    private Person person;

    public GeneralCustomerForm() {
        birthdate.setLabel("Geburtsdatum");

        personBinder.forField(birthdate).withConverter(new LocalDateToDateConverter()).bind(Person::getBirthdate, Person::setBirthdate);
        personBinder.bindInstanceFields(this);

        add(
                firstName,
                lastName,
                birthdate,
                phoneNumber
        );
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        personBinder.readBean(person);
    }

    public void validateAndSave() throws ValidationException {
        personBinder.writeBean(person);
    }
}
