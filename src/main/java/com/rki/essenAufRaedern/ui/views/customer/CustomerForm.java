package com.rki.essenAufRaedern.ui.views.customer;

import com.rki.essenAufRaedern.backend.entity.Company;
import com.rki.essenAufRaedern.backend.entity.ContactPerson;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class CustomerForm extends FormLayout{

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");
    //DatePicker birthdate = new DatePicker("Geboren am");
    EmailField email = new EmailField("Email");
    TextField firstNameContact = new TextField("Kontaktperson Vorname");
    TextField lastNameContact = new TextField("Kontaktperson Nachname");
    //ComboBox<Person.Status> status = new ComboBox<>("Status");
    //ComboBox<Company> company = new ComboBox<>("Company");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Person> binder = new BeanValidationBinder<>(Person.class);
    private Person person;


    public CustomerForm() {
        addClassName("customer-form");

        binder.bindInstanceFields(this);
        //status.setItems(Person.Status.values());
        //company.setItems(companies);
        //company.setItemLabelGenerator(Company::getName);

        add(
                firstName,
                lastName,
                //birthdate,
                email,
                firstNameContact,
                lastNameContact,
                createButtonsLayout()
        );
    }

    public void setPerson(Person person) {
        this.person = person;
        binder.readBean(person);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new com.rki.essenAufRaedern.ui.views.customer.CustomerForm.DeleteEvent(this, person)));
        close.addClickListener(click -> fireEvent(new com.rki.essenAufRaedern.ui.views.customer.CustomerForm.CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {

        try {
            binder.writeBean(person);
            fireEvent(new com.rki.essenAufRaedern.ui.views.customer.CustomerForm.SaveEvent(this, person));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class CustomerFormEvent extends ComponentEvent<com.rki.essenAufRaedern.ui.views.customer.CustomerForm> {
        private Person person;

        protected CustomerFormEvent(com.rki.essenAufRaedern.ui.views.customer.CustomerForm source, Person person) {
            super(source, false);
            this.person = person;
        }

        public Person getPerson() {
            return person;
        }
    }

    public static class SaveEvent extends com.rki.essenAufRaedern.ui.views.customer.CustomerForm.CustomerFormEvent {
        SaveEvent(com.rki.essenAufRaedern.ui.views.customer.CustomerForm source, Person person) {
            super(source, person);
        }
    }

    public static class DeleteEvent extends com.rki.essenAufRaedern.ui.views.customer.CustomerForm.CustomerFormEvent {
        DeleteEvent(com.rki.essenAufRaedern.ui.views.customer.CustomerForm source, Person person) {
            super(source, person);
        }

    }

    public static class CloseEvent extends com.rki.essenAufRaedern.ui.views.customer.CustomerForm.CustomerFormEvent {
        CloseEvent(com.rki.essenAufRaedern.ui.views.customer.CustomerForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
