package com.rki.essenAufRaedern.ui.components.person;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class AddressEditorComponent extends FormLayout {

    private final TextField street = new TextField("Strasse");
    private final TextField houseNumber = new TextField("Hausnummer");
    private final TextField floor = new TextField("Stock");
    private final TextField zipCode = new TextField("PLZ");
    private final TextField city = new TextField("Stadt");
    private final TextField country = new TextField("Land");

    private final Binder<Address> addressBinder = new BeanValidationBinder<>(Address.class);
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
        return address;
    }

    public void validateAndSave() throws ValidationException {
        addressBinder.writeBean(address);
    }
}
