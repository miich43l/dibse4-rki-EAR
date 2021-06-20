package com.rki.essenAufRaedern.ui.components.orders;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

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
        HorizontalLayout mainLayout = new HorizontalLayout();

        VerticalLayout layoutLeft = new VerticalLayout();
        layoutLeft.setWidthFull();

        Icon icon = VaadinIcon.USER.create();
        icon.setColor("red");
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

        layoutLeft.setDefaultHorizontalComponentAlignment(Alignment.START);
        layoutLeft.setPadding(false);
        layoutLeft.add(nameLayout, addressLayout);
        mainLayout.add(layoutLeft);

        Button infoButton = new Button("");
        infoButton.setIcon(VaadinIcon.INFO.create());
        infoButton.addClickListener(event -> onInfoButtonPressed());
        mainLayout.addAndExpand(infoButton);

        return mainLayout;
    }

    private void onInfoButtonPressed() {
        fireEvent(new OrderDeliveryWidget.InfoButtonPressedEvent(this, orderBinder.getBean()));
    }


    // -- EVENTS:
    public static abstract class Event extends ComponentEvent<OrderDeliveryWidget> {
        private Order order;

        protected Event(OrderDeliveryWidget source, Order order) {
            super(source, false);
            this.order = order;
        }

        public Order getOrder() {
            return order;
        }
    }

    public static class InfoButtonPressedEvent extends OrderDeliveryWidget.Event {
        protected InfoButtonPressedEvent(OrderDeliveryWidget source, Order order) {
            super(source, order);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
