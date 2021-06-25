package com.rki.essenAufRaedern.ui.components.person;

import ch.qos.logback.core.Layout;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;

public class DeliveryDaysComponent extends FormLayout {

    CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();



    public DeliveryDaysComponent(){
        checkboxGroup.setLabel("Liefertage");
        checkboxGroup.setItems("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag");
        //checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        add(checkboxGroup);
    }

}
