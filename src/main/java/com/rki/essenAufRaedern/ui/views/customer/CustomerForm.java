package com.rki.essenAufRaedern.ui.views.customer;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.ui.components.address.AddressEditorComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.shared.Registration;

public class CustomerForm extends FormLayout{

    TextField firstName = new TextField("Vorname");
    TextField lastName = new TextField("Nachname");

    DatePicker birthdate = new DatePicker();
    EmailField email = new EmailField("Email");
    TextField firstNameContact = new TextField("Kontaktperson Vorname");
    TextField lastNameContact = new TextField("Kontaktperson Nachname");
    AddressEditorComponent addressEditorComponent = new AddressEditorComponent();

    //ComboBox<Person.Status> status = new ComboBox<>("Status");
    //ComboBox<Company> company = new ComboBox<>("Company");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Person> personBinder = new BeanValidationBinder<>(Person.class);
    private Person person;



    public CustomerForm() {
        addClassName("customer-form");

        birthdate.setLabel("Geburtsdatum");

        personBinder.forField(birthdate).withConverter(new LocalDateToDateConverter()).bind(Person::getBirthdate, Person::setBirthdate);
        personBinder.bindInstanceFields(this);


        //status.setItems(Person.Status.values());
        //company.setItems(companies);
        //company.setItemLabelGenerator(Company::getName);

        add(
                firstName,
                lastName,
                birthdate,
                email,
                firstNameContact,
                lastNameContact,
                addressEditorComponent,
                createButtonsLayout()
        );
    }

    public void setPerson(Person person) {
        this.person = person;
        personBinder.readBean(person);
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

        personBinder.addStatusChangeListener(evt -> save.setEnabled(personBinder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {

        try {
            personBinder.writeBean(person);
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
