package com.rki.essenAufRaedern.ui.components.person;

import com.rki.essenAufRaedern.backend.entity.ContactPerson;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;

public class ContactPersonComponent extends VerticalLayout {
    private Person person;
    private Grid<ContactPerson> contactPersonGrid = new Grid();
    private Config config;

    public static class Config {

        public Config allowDelete(boolean deleteAllowed) {
            this.deleteAllowed = deleteAllowed;
            return this;
        }

        public Config allowCall(boolean callAllowed) {
            this.callAllowed = callAllowed;
            return this;
        }

        public boolean isActionColumnNeeded() {
            return callAllowed || deleteAllowed;
        }

        boolean callAllowed = false;
        boolean deleteAllowed = false;
    }

    public ContactPersonComponent(Config config) {
        this.config = config;

        add(createContactPersonComponent());
        setPadding(false);
    }

    public void setPerson(Person person) {
        this.person = person;
        this.contactPersonGrid.setItems(person.getContactPersons());
    }

    private Component createContactPersonComponent() {
        VerticalLayout mainLayout = new VerticalLayout();

        contactPersonGrid.addColumn(new ComponentRenderer<>(info -> new Text(info.getContactPersonType().toString()))).setHeader("Type").setKey("type");
        contactPersonGrid.addColumn(item -> item.getContactPersonFrom().getFullName()).setHeader("Name");

        if(config.isActionColumnNeeded()) {
            contactPersonGrid.addColumn(new ComponentRenderer<>(info -> {
                HorizontalLayout layout = new HorizontalLayout();

                if(config.deleteAllowed) {
                    Icon icon = VaadinIcon.TRASH.create();
                    icon.setColor("red");
                    Button deleteButton = new Button(icon);
                    deleteButton.addClickListener(e ->{fireEvent(new ContactPersonComponent.DeleteButtonPressedEvent(this, info));});
                    layout.add(deleteButton);
                }

                if(config.callAllowed) {
                    Icon icon = VaadinIcon.PHONE.create();
                    icon.setColor("red");
                    Button deleteButton = new Button(icon);
                    deleteButton.addClickListener(e ->{fireEvent(new CallButtonPressedEvent(this, info));});
                    layout.add(deleteButton);
                }

                return layout;
            })).setHeader("Actions").setKey("actions");
        }

        mainLayout.add(contactPersonGrid);
        mainLayout.setPadding(false);
        return mainLayout;
    }

    //Events
    public static abstract class Event extends ComponentEvent<ContactPersonComponent> {
        private final ContactPerson contactPerson;

        protected Event(ContactPersonComponent source, ContactPerson contactPerson) {
            super(source, false);
            this.contactPerson = contactPerson;
        }

        public ContactPerson getContactPerson() {
            return contactPerson;
        }
    }

    public static class DeleteButtonPressedEvent extends ContactPersonComponent.Event {
        protected DeleteButtonPressedEvent(ContactPersonComponent source, ContactPerson contactPerson) {
            super(source, contactPerson);
        }
    }

    public static class CallButtonPressedEvent extends ContactPersonComponent.Event {
        public CallButtonPressedEvent(ContactPersonComponent contactPersonComponent, ContactPerson contactPerson) {
            super(contactPersonComponent, contactPerson);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
