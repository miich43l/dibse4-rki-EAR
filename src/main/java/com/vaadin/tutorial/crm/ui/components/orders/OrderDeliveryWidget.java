package com.vaadin.tutorial.crm.ui.components.orders;

import com.rki.essenAufReaedern.backend.entity.Address;
import com.rki.essenAufReaedern.backend.entity.Order;
import com.rki.essenAufReaedern.backend.entity.Person;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;

public class OrderDeliveryWidget extends VerticalLayout {
    private Label personNameLabel = new Label("Name");
    private Label addressLabel = new Label("Address");

    private Binder<Order> orderBinder = new Binder<>(Order.class);
    private Binder<Person> personBinder = new Binder<>(Person.class);

    public OrderDeliveryWidget(Order order) {
        orderBinder.setBean(order);
        personBinder.setBean(order.getPerson());

        orderBinder.addValueChangeListener(event -> personBinder.setBean(((Order)event.getValue()).getPerson()));

        add(createPersonComponent());

        setPadding(false);
        setMargin(false);
    }

    private Component createPersonComponent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        Icon icon = VaadinIcon.USER.create();
        Person person = personBinder.getBean();

        personNameLabel.setText(person.getFirstName() + " " + person.getLastName());

        if(personBinder.getBean().getAddress() != null) {
            Address address = personBinder.getBean().getAddress();
            addressLabel.setText(address.getCity() + " - " + address.getStreet() + " " + address.getHouseNumber());
        } else {
            addressLabel.setText("-");
        }

        HorizontalLayout nameLayout = new HorizontalLayout(icon, personNameLabel);
        nameLayout.setPadding(false);
        VerticalLayout addressLayout = new VerticalLayout(addressLabel);
        addressLayout.setMargin(false);
        addressLayout.setPadding(false);
        addressLayout.setWidthFull();

        layout.setDefaultHorizontalComponentAlignment(Alignment.START);
        layout.setPadding(false);
        layout.add(nameLayout, addressLayout);
        return layout;
    }
}
