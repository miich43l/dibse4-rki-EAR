package com.rki.essenAufRaedern.ui.components.person;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import com.rki.essenAufRaedern.ui.components.orders.OrderDeliveryWidget;
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
import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

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

    public void setActionColumnVisible(boolean visible){
        infoGrid.getColumnByKey("actions").setVisible(visible);
    }

    public void setTypeColumnVisible(boolean visible){
        infoGrid.getColumnByKey("type").setVisible(visible);
    }

    private Component createInfoComponent() { VerticalLayout mainLayout = new VerticalLayout();

        infoGrid.addColumn(new ComponentRenderer<>(info -> new Text(info.getInformationType().toString()))).setHeader("Typ").setKey("type");
        infoGrid.addColumn(AdditionalInformation::getValue).setHeader("Information");
        infoGrid.addColumn(new ComponentRenderer<>(info -> {
            Icon icon = VaadinIcon.TRASH.create();
            icon.setColor("red");
            Button deleteButton = new Button(icon);
            deleteButton.addClickListener(e ->{System.out.println("before fire event");fireEvent(new DeleteButtonPressedEvent(this, info));});
            return deleteButton;
        })).setHeader("Aktion").setKey("actions");

        mainLayout.add(infoGrid);
        mainLayout.setPadding(false);
        return mainLayout;
    }

    //Events
    public static abstract class Event extends ComponentEvent<AdditionalInformationComponent> {
        private AdditionalInformation additionalInformation;

        protected Event(AdditionalInformationComponent source, AdditionalInformation additionalInformation) {
            super(source, false);
            this.additionalInformation = additionalInformation;
        }

        public AdditionalInformation getAdditionalInformation() {
            return additionalInformation;
        }
    }

    public static class DeleteButtonPressedEvent extends AdditionalInformationComponent.Event {
        protected DeleteButtonPressedEvent(AdditionalInformationComponent source, AdditionalInformation additionalInformation) {
            super(source, additionalInformation);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}

