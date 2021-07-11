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

    private final TextField firstName = new TextField("Vorname");
    private final TextField lastName = new TextField("Nachname");

    private final DatePicker birthdate = new DatePicker();
    private final TextField phoneNumber = new TextField("Telefonnummer");

    private final Binder<Person> personBinder = new BeanValidationBinder<>(Person.class);
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
