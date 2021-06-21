package com.rki.essenAufRaedern.ui.components.person;

import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.entity.Person;

public class AdditionalInformationComponent extends VerticalLayout {

    private Person person;
    private Grid<AdditionalInformation> infoGrid = new Grid();

    private InformationType informationTypeFilter = null;

    public AdditionalInformationComponent() {
        add(createInfoComponent());
        setPadding(false);
    }

    public void setPerson(Person person) {
        this.person = person;
        this.infoGrid.setItems(informationTypeFilter == null ? person.getAdditionalInformation()
                                                             : person.getAdditionalInformation(informationTypeFilter));
    }

    public void setFilterType(InformationType type) {
        this.informationTypeFilter = type;
    }

    private Component createInfoComponent() {
        VerticalLayout mainLayout = new VerticalLayout();

        infoGrid.addColumn(new ComponentRenderer<>(info -> new Text(info.getInformationType().toString()))).setHeader("Type");
        infoGrid.addColumn(AdditionalInformation::getValue).setHeader("Information");

        mainLayout.add(infoGrid);
        mainLayout.setPadding(false);
        return mainLayout;
    }
}

