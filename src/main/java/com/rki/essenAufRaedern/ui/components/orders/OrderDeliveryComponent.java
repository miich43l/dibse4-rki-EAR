package com.rki.essenAufRaedern.ui.components.orders;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class OrderDeliveryComponent extends VerticalLayout {
    private final H5 personNameLabel = new H5("Name");
    private final H6 addressLabel = new H6("Address");

    private final Binder<Order> orderBinder = new Binder<>(Order.class);
    private final Binder<Person> personBinder = new Binder<>(Person.class);

    public OrderDeliveryComponent(Order order) {
        orderBinder.setBean(order);
        personBinder.setBean(order.getPerson());

        orderBinder.addValueChangeListener(event -> personBinder.setBean(((Order)event.getValue()).getPerson()));

        add(createPersonComponent());

        setPadding(false);
        setMargin(false);
    }

    private Component createPersonComponent() {
        HorizontalLayout mainLayout = new HorizontalLayout();

        VerticalLayout layoutLeft = new VerticalLayout();
        layoutLeft.setWidthFull();

        Person person = personBinder.getBean();

        personNameLabel.setText(person.getFirstName() + " " + person.getLastName());

        if(personBinder.getBean().getAddress() != null) {
            Address address = personBinder.getBean().getAddress();
            addressLabel.setText(address.getCity() + " - " + address.getStreet() + " " + address.getHouseNumber());
        } else {
            addressLabel.setText("-");
        }

        layoutLeft.setDefaultHorizontalComponentAlignment(Alignment.START);
        layoutLeft.setPadding(false);
        layoutLeft.add(personNameLabel, addressLabel);
        mainLayout.add(layoutLeft);

        Button infoButton = new Button("");
        infoButton.setIcon(VaadinIcon.INFO.create());
        infoButton.addClickListener(event -> onInfoButtonPressed());
        mainLayout.addAndExpand(infoButton);

        return mainLayout;
    }

    private void onInfoButtonPressed() {
        fireEvent(new OrderDeliveryComponent.InfoButtonPressedEvent(this, orderBinder.getBean()));
    }


    // -- EVENTS:
    public static abstract class Event extends ComponentEvent<OrderDeliveryComponent> {
        private Order order;

        protected Event(OrderDeliveryComponent source, Order order) {
            super(source, false);
            this.order = order;
        }

        public Order getOrder() {
            return order;
        }
    }

    public static class InfoButtonPressedEvent extends OrderDeliveryComponent.Event {
        protected InfoButtonPressedEvent(OrderDeliveryComponent source, Order order) {
            super(source, order);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
