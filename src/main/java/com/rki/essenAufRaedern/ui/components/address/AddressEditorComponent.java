package com.rki.essenAufRaedern.ui.components.address;

import ch.qos.logback.core.Layout;
import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.ui.components.orders.OrderDeliveryWidget;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.VerticalAlign;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class AddressEditorComponent extends FormLayout {

    TextField street = new TextField("Strasse");
    TextField houseNumber = new TextField("Hausnummer");
    TextField floor = new TextField("Stock");
    TextField zipCode = new TextField("PLZ");
    TextField city = new TextField("Stadt");
    TextField country = new TextField("Land");

    Binder<Address> addressBinder = new BeanValidationBinder<>(Address.class);
    private Address address = new Address();

    public AddressEditorComponent() {
        addClassName("address-editor");
        setWidthFull();

        addressBinder.bindInstanceFields(this);

        add(
                street,
                houseNumber,
                floor,
                zipCode,
                city,
                country
        );
    }


    public boolean isValid() {
        return addressBinder.isValid();
    }

    public void setAddress(Address address) {
        this.address = address;
        addressBinder.readBean(address);
    }

    public Address getAddress() {
        try {
            addressBinder.writeBean(address);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        return address;
    }
}
