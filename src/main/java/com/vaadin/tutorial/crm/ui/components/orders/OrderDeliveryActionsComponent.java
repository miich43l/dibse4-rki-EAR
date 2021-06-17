package com.vaadin.tutorial.crm.ui.components.orders;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entity.Order;

public class OrderDeliveryActionsComponent extends VerticalLayout {
    private Button deliveredButton = new Button("Geliefert");
    private Button notDeliveredButton = new Button("Nicht Geliefert");
    private Button callContactPersonButton = new Button("Kontaktperson");

    private Binder<Order> binder = new Binder(Order.class);

    public OrderDeliveryActionsComponent(Order order) {
        binder.setBean(order);
        binder.addValueChangeListener(event -> updateUI());

        addEventListener();

        deliveredButton.setWidthFull();
        notDeliveredButton.setWidthFull();
        callContactPersonButton.setWidthFull();

        add(deliveredButton, notDeliveredButton, callContactPersonButton);

        updateUI();
    }
    private void addEventListener() {
        deliveredButton.addClickListener(event -> fireEvent(new DeliveredEvent(this, binder.getBean())));
        notDeliveredButton.addClickListener(event -> fireEvent(new NotDeliveredEvent(this, binder.getBean())));
        callContactPersonButton.addClickListener(event -> fireEvent(new CallContactPersonEvent(this, binder.getBean())));
    }

    private  void updateUI() {
        //TODO: request status...
    }

    // -- EVENTS:
    public static abstract class OrderDeliveryActionEvent extends ComponentEvent<OrderDeliveryActionsComponent> {
        private Order order;

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
