package com.rki.essenAufRaedern.ui.components.person;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class AdditionalInformationForm extends FormLayout {

    ComboBox<InformationType> informationType = new ComboBox<>("Typ");
    TextField value = new TextField("Information");

    Binder<AdditionalInformation> additionalInformationBinder = new BeanValidationBinder<>(AdditionalInformation.class);
    private AdditionalInformation additionalInformation;

    public AdditionalInformationForm() {
        additionalInformationBinder.bindInstanceFields(this);
        informationType.setItems(InformationType.values());

        add(
                informationType,
                value
        );
    }

    public AdditionalInformation getAdditionalInformation() {
        try {
            additionalInformationBinder.writeBean(additionalInformation);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
        additionalInformationBinder.readBean(additionalInformation);
    }

    public boolean isValid() {
        return additionalInformationBinder.isValid();
    }
}
