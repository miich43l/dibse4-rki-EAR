package com.vaadin.tutorial.crm.ui.components.orders;

import com.rki.essenAufReaedern.backend.entity.Order;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Set;

public class OrderDeliveriesWidget extends VerticalLayout {

    private final Grid<Order> ordersGrid = new Grid<>();

    public OrderDeliveriesWidget() {
        add(new Text("Lieferungen"), ordersGrid);
        ordersGrid.addColumn(new ComponentRenderer<>(OrderDeliveryWidget::new)).setAutoWidth(true);
        ordersGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        ordersGrid.addItemClickListener(e -> this.onDidSelectOrderItem());

        ordersGrid.setItemDetailsRenderer(new ComponentRenderer<OrderDeliveryActionsComponent, Order>(item -> {
            OrderDeliveryActionsComponent actionsComponent = new OrderDeliveryActionsComponent(item);

            actionsComponent.addListener(OrderDeliveryActionsComponent.StartEvent.class, e -> this.onStartDelivery(item));
            actionsComponent.addListener(OrderDeliveryActionsComponent.EndEvent.class, e -> this.onEndDelivery(item));

            return actionsComponent;
        }));
    }

    public void setOrders(List<Order> orders) {
        ordersGrid.setItems(orders);
    }

    private void onDidSelectOrderItem() {
        Set<Order> selectedItems = ordersGrid.getSelectedItems();
        if(selectedItems.isEmpty()) {
            return;
        }

        Order selectedOrder = selectedItems.iterator().next();
        fireEvent(new DidSelectEvent(this, selectedOrder));
    }

    private void onStartDelivery(Order item) {
        fireEvent(new StartEvent(this, item));
    }

    private void onEndDelivery(Order item) {
        fireEvent(new EndEvent(this, item));
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

    public static class StartEvent extends Event {
        protected StartEvent(OrderDeliveriesWidget source, Order order) {
            super(source, order);
        }
    }

    public static class EndEvent extends Event {
        protected EndEvent(OrderDeliveriesWidget source, Order order) {
            super(source, order);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
