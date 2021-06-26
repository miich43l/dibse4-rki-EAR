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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;

public class ContactPersonComponent extends VerticalLayout {
    private Person person;
    private Grid<ContactPerson> contactPersonGrid = new Grid();

    public ContactPersonComponent() {
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
        contactPersonGrid.addColumn(new ComponentRenderer<>(info -> {
            Icon icon = VaadinIcon.TRASH.create();
            icon.setColor("red");
            Button deleteButton = new Button(icon);
            deleteButton.addClickListener(e ->{System.out.println("before fire event");fireEvent(new ContactPersonComponent.DeleteButtonPressedEvent(this, info));});
            return deleteButton;
        })).setHeader("Actions").setKey("actions");

        mainLayout.add(contactPersonGrid);
        mainLayout.setPadding(false);
        return mainLayout;
    }

    //Events
    public static abstract class Event extends ComponentEvent<ContactPersonComponent> {
        private ContactPerson contactPerson;

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

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
