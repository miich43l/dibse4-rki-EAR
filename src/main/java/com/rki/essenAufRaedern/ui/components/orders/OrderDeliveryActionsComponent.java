package com.rki.essenAufRaedern.ui.components.orders;

import com.rki.essenAufRaedern.backend.entity.Order;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class OrderDeliveryActionsComponent extends HorizontalLayout {
    private Button startButton = new Button("Start");
    private Button endButton = new Button("Fertig");

    private Binder<Order> binder = new Binder(Order.class);

    public OrderDeliveryActionsComponent(Order order) {
        binder.setBean(order);
        binder.addValueChangeListener(event -> updateUI());

        addEventListener();

        add(startButton, endButton);

        updateUI();
    }
    private void addEventListener() {
        startButton.addClickListener(event -> fireEvent(new StartEvent(this, binder.getBean())));
        endButton.addClickListener(event -> fireEvent(new EndEvent(this, binder.getBean())));
    }

    private  void updateUI() {
        //TODO: request status...
        startButton.setEnabled(true);
        endButton.setEnabled(false);
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

    public static class StartEvent extends OrderDeliveryActionsComponent.OrderDeliveryActionEvent {
        protected StartEvent(OrderDeliveryActionsComponent source, Order order) {
            super(source, order);
        }
    }

    public static class EndEvent extends OrderDeliveryActionsComponent.OrderDeliveryActionEvent {
        protected EndEvent(OrderDeliveryActionsComponent source, Order order) {
            super(source, order);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
