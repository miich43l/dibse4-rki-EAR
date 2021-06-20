package com.vaadin.tutorial.crm.ui.components.person;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class AdditionalInformationComponent extends VerticalLayout {

    private Person person;
    private Grid<AdditionalInformation> infoGrid = new Grid();

    public AdditionalInformationComponent() {
        add(createInfoComponent());
        setPadding(false);
    }

    public void setPerson(Person person) {
        this.person = person;
        this.infoGrid.setItems(person.getAdditionalInformation());
    }

    private Component createInfoComponent() {
        VerticalLayout mainLayout = new VerticalLayout();

        infoGrid.addColumn(new ComponentRenderer<>(info -> {
            return new Text("TODO");
        })).setHeader("Type");

        infoGrid.addColumn(AdditionalInformation::getValue).setHeader("Information");

        mainLayout.add(infoGrid);
        mainLayout.setPadding(false);
        return mainLayout;
    }
}
