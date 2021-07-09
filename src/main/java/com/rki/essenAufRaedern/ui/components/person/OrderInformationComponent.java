package com.rki.essenAufRaedern.ui.components.person;

import com.rki.essenAufRaedern.backend.entity.OrderInformation;
import com.rki.essenAufRaedern.backend.utility.Status;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.Div;

public class OrderInformationComponent extends Div {

    CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
    private OrderInformation orderInformation = new OrderInformation();

    public OrderInformationComponent() {
        addClassName("orderInformation-editor");

        checkboxGroup.setLabel("Liefertage");
        checkboxGroup.setItems("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag");
        checkboxGroup.setWidthFull();
        add(checkboxGroup);
    }

    public void setOrderInformation(OrderInformation orderInformation) {
        this.orderInformation = orderInformation;
        updateUI();
    }

    public OrderInformation getOrderInformation() {
        updateEntity();
        return orderInformation;
    }

    private void updateUI() {
        readDayOfWeekInformationAndUpdateUi(orderInformation.getMonday(), "Montag");
        readDayOfWeekInformationAndUpdateUi(orderInformation.getTuesday(), "Dienstag");
        readDayOfWeekInformationAndUpdateUi(orderInformation.getWednesday(), "Mittwoch");
        readDayOfWeekInformationAndUpdateUi(orderInformation.getThursday(), "Donnerstag");
        readDayOfWeekInformationAndUpdateUi(orderInformation.getFriday(), "Freitag");
        readDayOfWeekInformationAndUpdateUi(orderInformation.getSaturday(), "Samstag");
        readDayOfWeekInformationAndUpdateUi(orderInformation.getSunday(), "Sonntag");

    }

    private void updateEntity() {
        orderInformation.setMonday(isDayOfWeekSelected("Montag"));
        orderInformation.setTuesday(isDayOfWeekSelected("Dienstag"));
        orderInformation.setWednesday(isDayOfWeekSelected("Mittwoch"));
        orderInformation.setThursday(isDayOfWeekSelected("Donnerstag"));
        orderInformation.setFriday(isDayOfWeekSelected("Freitag"));
        orderInformation.setSaturday(isDayOfWeekSelected("Samstag"));
        orderInformation.setSunday(isDayOfWeekSelected("Sonntag"));
    }

    private void readDayOfWeekInformationAndUpdateUi(Status status, String dayOfWeek) {
        if (status == Status.ACTIVE) {
            checkboxGroup.select(dayOfWeek);
        } else {
            checkboxGroup.deselect(dayOfWeek);
        }
    }

    private Status isDayOfWeekSelected(String dayOfWeek) {
        if (checkboxGroup.isSelected(dayOfWeek)) {
            return Status.ACTIVE;
        } else {
            return Status.INACTIVE;
        }
    }
}


