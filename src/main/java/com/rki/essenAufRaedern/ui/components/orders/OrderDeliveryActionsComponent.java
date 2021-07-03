package com.rki.essenAufRaedern.ui.components.orders;

import com.rki.essenAufRaedern.backend.entity.Order;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class OrderDeliveryActionsComponent extends VerticalLayout {
    private final Button deliveredButton = new Button("Geliefert");
    private final Button notDeliveredButton = new Button("Nicht Geliefert");
    private final Button callContactPersonButton = new Button("Kontaktperson");

    private final Order order;

    public OrderDeliveryActionsComponent(Order order) {
        this.order = order;

        addEventListener();

        deliveredButton.setWidthFull();
        notDeliveredButton.setWidthFull();
        callContactPersonButton.setWidthFull();

        add(deliveredButton, notDeliveredButton, callContactPersonButton);
    }
    private void addEventListener() {
        deliveredButton.addClickListener(event -> fireEvent(new DeliveredEvent(this, order)));
        notDeliveredButton.addClickListener(event -> fireEvent(new NotDeliveredEvent(this, order)));
        callContactPersonButton.addClickListener(event -> fireEvent(new CallContactPersonEvent(this, order)));
    }

    // -- EVENTS:
    public static abstract class OrderDeliveryActionEvent extends ComponentEvent<OrderDeliveryActionsComponent> {
        private final Order order;

        protected OrderDeliveryActionEvent(OrderDeliveryActionsComponent source, Order order) {
            super(source, false);
            this.order = order;
        }

        public Order getOrder() {
            return order;
        }
    }

    public static class DeliveredEvent extends OrderDeliveryActionsComponent.OrderDeliveryActionEvent {
        protected DeliveredEvent(OrderDeliveryActionsComponent source, Order order) {
            super(source, order);
        }
    }

    public static class NotDeliveredEvent extends OrderDeliveryActionsComponent.OrderDeliveryActionEvent {
        protected NotDeliveredEvent(OrderDeliveryActionsComponent source, Order order) {
            super(source, order);
        }
    }

    public static class CallContactPersonEvent extends OrderDeliveryActionsComponent.OrderDeliveryActionEvent {
        protected CallContactPersonEvent(OrderDeliveryActionsComponent source, Order order) {
            super(source, order);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
