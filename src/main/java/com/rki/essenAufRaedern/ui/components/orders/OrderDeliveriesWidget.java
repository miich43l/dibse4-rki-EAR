package com.rki.essenAufRaedern.ui.components.orders;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;
import com.rki.essenAufRaedern.backend.entity.Order;

import java.util.List;
import java.util.Set;

public class OrderDeliveriesWidget extends VerticalLayout {

    private final Grid<Order> ordersGrid = new Grid<>();
    private List<Order> orders;

    public OrderDeliveriesWidget() {
        ordersGrid.addColumn(new ComponentRenderer<>(item -> {
            OrderDeliveryWidget comp = new OrderDeliveryWidget(item);
            comp.addListener(OrderDeliveryWidget.InfoButtonPressedEvent.class, e -> this.onAdditionalInfoButtonPressed(e.getOrder()));
            return comp;
        })).setAutoWidth(true);
        ordersGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        ordersGrid.addItemClickListener(e -> this.onDidSelectOrderItem());

        ordersGrid.setItemDetailsRenderer(new ComponentRenderer<OrderDeliveryActionsComponent, Order>(item -> {
            OrderDeliveryActionsComponent actionsComponent = new OrderDeliveryActionsComponent(item);

            actionsComponent.addListener(OrderDeliveryActionsComponent.DeliveredEvent.class, e -> this.onDeliveredPressed(item));
            actionsComponent.addListener(OrderDeliveryActionsComponent.NotDeliveredEvent.class, e -> this.onNotDeliveredPressed(item));
            actionsComponent.addListener(OrderDeliveryActionsComponent.CallContactPersonEvent.class, e -> this.onCallContactPersonPressed(item));

            return actionsComponent;
        }));

        add(ordersGrid);
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        ordersGrid.setItems(orders);
    }

    private void onDidSelectOrderItem() {
        Set<Order> selectedItems = ordersGrid.getSelectedItems();
        if(selectedItems.isEmpty()) {
            return;
        }

        Order selectedOrder = selectedItems.iterator().next();
        int index = orders.indexOf(selectedOrder);
        ordersGrid.scrollToIndex(index);

        fireEvent(new DidSelectEvent(this, selectedOrder));
    }

    private void onAdditionalInfoButtonPressed(Order item) {
        fireEvent(new InfoButtonPressedEvent(this, item));
    }

    private void onDeliveredPressed(Order item) {
        fireEvent(new DeliveredEvent(this, item));
    }

    private void onNotDeliveredPressed(Order item) {
        fireEvent(new NotDeliveredEvent(this, item));
    }

    private void onCallContactPersonPressed(Order item) {
        fireEvent(new CallContactPersonEvent(this, item));
    }

    // -- EVENTS:
    public static abstract class Event extends ComponentEvent<OrderDeliveriesWidget> {
        private Order order;

        protected Event(OrderDeliveriesWidget source, Order order) {
            super(source, false);
            this.order = order;
        }

        public Order getOrder() {
            return order;
        }
    }

    public static class DidSelectEvent extends Event {
        protected DidSelectEvent(OrderDeliveriesWidget source, Order order) {
            super(source, order);
        }
    }

    public static class InfoButtonPressedEvent extends Event {
        protected InfoButtonPressedEvent(OrderDeliveriesWidget source, Order order) {
            super(source, order);
        }
    }

    public static class DeliveredEvent extends Event {
        protected DeliveredEvent(OrderDeliveriesWidget source, Order order) {
            super(source, order);
        }
    }

    public static class NotDeliveredEvent extends Event {
        protected NotDeliveredEvent(OrderDeliveriesWidget source, Order order) {
            super(source, order);
        }
    }

    public static class CallContactPersonEvent extends Event {
        protected CallContactPersonEvent(OrderDeliveriesWidget source, Order order) {
            super(source, order);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
