package com.rki.essenAufRaedern.ui.components.address;

import ch.qos.logback.core.Layout;
import com.rki.essenAufRaedern.backend.entity.Address;
import com.vaadin.flow.component.charts.model.VerticalAlign;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

public class AddressEditorComponent extends VerticalLayout {

    TextField street = new TextField("Strasse");
    TextField houseNumber = new TextField("Hausnummer");
    TextField floor = new TextField("Stock");
    TextField zipCode = new TextField("PLZ");
    TextField city = new TextField("Stadt");
    TextField country = new TextField("Land");


    Binder<Address> addressBinder = new BeanValidationBinder<>(Address.class);
    private Address address;

    public AddressEditorComponent() {
        addClassName("address-editor");
        setWidthFull();
        add(
                street,
                houseNumber,
                floor,
                zipCode,
                city,
                country
        );
    }


}
