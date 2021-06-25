package com.rki.essenAufRaedern.ui.components.person;

import ch.qos.logback.core.Layout;
import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.OrderInformation;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

public class OrderInformationComponent extends FormLayout {

    CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();

    Binder<OrderInformation> orderBinder = new BeanValidationBinder<>(OrderInformation.class);
    private OrderInformation orderInformation = new OrderInformation();



    public OrderInformationComponent(){
        addClassName("orderInformation-editor");

        checkboxGroup.setLabel("Liefertage");
        checkboxGroup.setItems("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag");
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        setWidthFull();
        //orderBinder.bindInstanceFields(this);

        add(checkboxGroup);
    }

}
