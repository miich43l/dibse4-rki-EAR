package com.rki.essenAufRaedern.ui.views.delivery;

import com.rki.essenAufRaedern.algorithm.tsp.TSP;
import com.rki.essenAufRaedern.algorithm.tsp.api.IRoutingService;
import com.rki.essenAufRaedern.algorithm.tsp.api.RoutingServiceFactory;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPath;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;
import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.service.AddressService;
import com.rki.essenAufRaedern.backend.service.KitchenService;
import com.rki.essenAufRaedern.backend.service.OrderService;
import com.rki.essenAufRaedern.backend.service.PersonService;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.olmap.OLMap;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapMarker;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapRoute;
import com.rki.essenAufRaedern.ui.components.orders.OrderDeliveriesWidget;
import com.rki.essenAufRaedern.ui.components.person.AdditionalInformationComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Delivery")
@Route(value = "delivery", layout = MainLayout.class)
public class DeliveryView extends VerticalLayout {

    private OrderDeliveriesWidget deliveriesWidget = new OrderDeliveriesWidget();
    private OLMap mapComponent = new OLMap();

    private PersonService personService;
    private OrderService orderService;
    private KitchenService kitchenService;
    private AddressService addressService;

    private Kitchen kitchen;
    private List<Order> orders = new ArrayList<>();
    private Map<Long, OLMapMarker> mapMarkers = new HashMap<>();
    private Map<Integer, Order> mapMarkerToOrder = new HashMap<>();
    private OLMapMarker kitchenMarker;

    public DeliveryView(KitchenService kitchenService, AddressService addressService, OrderService orderService) {
        this.kitchenService = kitchenService;
        this.addressService = addressService;
        this.orderService = orderService;

        addClassName("delivery-view");
        setSizeFull();

        setWidthFull();
        setHeight("85vh");
        setPadding(false);

        loadData();

        add(createMainLayout());

        addEventListener();
    }

    private void loadData() {
        List<Kitchen> kitchens = kitchenService.findAll();
        this.kitchen = kitchens.get(0);

        Date date_ = new Date();

        //TODO: Use service!
        Predicate<Order> orderPredicate = order -> {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date_);

            Calendar c2 = Calendar.getInstance();
            c2.setTime(order.getDt());

            /*
            System.out.println(c1.get(Calendar.YEAR) + " / " + c2.get(Calendar.YEAR));
            System.out.println(c1.get(Calendar.MONTH) + " / " + c2.get(Calendar.MONTH));
            System.out.println(c1.get(Calendar.DAY_OF_MONTH) + " / " + c2.get(Calendar.DAY_OF_MONTH));
            System.out.println("active: " + order.getStatus());
            */

            return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                        && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                        && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
                    && (order.getDelivered() == null
                        && order.getNotDeliverable() == null);
        };

        orders = kitchen.getOrders().stream().filter(orderPredicate).collect(Collectors.toList());
    }

    private Component createMainLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setHeightFull();
        layout.add(createMapView(), createDeliveryListComponent());

        return layout;
    }

    private Component createMapView() {
        mapComponent.setHeight(100, Unit.PERCENTAGE);
        mapComponent.setWidthFull();
        mapComponent.addMarkerVisitedListener(e -> {
            if(!mapMarkerToOrder.containsKey(e.getMarkerId())) {
                return;
            }

            Order order = mapMarkerToOrder.get(e.getMarkerId());
            deliveriesWidget.selectOrder(order);
            Notification.show("Visited: " + order.getPerson().getFullName());
        });

        mapComponent.addMarkerLeaveListener(e -> {
            if(!mapMarkerToOrder.containsKey(e.getMarkerId())) {
                return;
            }

            Order order = mapMarkerToOrder.get(e.getMarkerId());
            Notification.show("Leave: " + order.getPerson().getFullName());
        });


        List<Point2D> pointsToVisit = new ArrayList<>();

        IRoutingService routingService = RoutingServiceFactory.get().createGraphHopperRoutingService();

        Point2D kitchenCoordinates = routingService.requestCoordinateFromAddress(kitchen.getAddress().toString());
        kitchenMarker = new OLMapMarker(kitchen.getName(), kitchenCoordinates, "house.png");
        mapComponent.addMarker(kitchenMarker);
        resetMapCenterAndZoom();

        pointsToVisit.add(kitchenCoordinates);

        // No orders => return;
        if(orders.isEmpty()) {
            return mapComponent;
        }

        for(Order order : orders) {
            Person person = order.getPerson();
            Address address = person.getAddress();
            String addressString = address.toString();
            Point2D coordinates = routingService.requestCoordinateFromAddress(addressString);
            OLMapMarker marker = createMapMarkerForOrder(order, coordinates);

            mapComponent.addMarker(marker);
            mapMarkers.put(order.getId(), marker);
            mapMarkerToOrder.put(marker.getId(), order);
            System.out.println("Marker: " + marker.getId() + " -> " + order.getPerson().getFullName());
            pointsToVisit.add(coordinates);
        }

        try {
            TSP tsp = new TSP();
            TspPathSequence tspSequence = tsp.calculateShortestPathSequence(pointsToVisit, 0);
            List<Point2D> tspSequenceCoordinates = tspSequence.getPath().stream().map(pointsToVisit::get).collect(Collectors.toList());
            TspPath tspRoute = tsp.calculatePathFromCoordinateList(tspSequenceCoordinates);
            OLMapRoute route = new OLMapRoute(tspRoute.getPoints());

            mapComponent.addRoute(route);
            mapComponent.startPositionSimulation(route);
        } catch (Exception exception) {
            Notification.show("Failed to calculate TSP!");
        }

        return mapComponent;
    }

    private OLMapMarker createMapMarkerForOrder(Order order, Point2D coordinates) {
        Person person = order.getPerson();
        String strIcons = "marker_red.png";

        if(order.getDelivered() != null) {
            strIcons = "marker_green.png";
        }

        if(order.getNotDeliverable() != null) {
            strIcons = "marker_gray.png";
        }

        return new OLMapMarker(person.getFullName(), coordinates, strIcons);
    }

    private void resetMapCenterAndZoom() {
        //mapComponent.setCenter(kitchenMarker.getCoordinates());
        mapComponent.lockPosition();
        mapComponent.setZoom(10);
    }

    private Component createDeliveryListComponent() {
        deliveriesWidget.setOrders(orders);
        deliveriesWidget.setWidthFull();
        deliveriesWidget.setMaxWidth(400, Unit.PIXELS);
        deliveriesWidget.setPadding(false);

        return deliveriesWidget;
    }

    private void addEventListener() {
        deliveriesWidget.addListener(OrderDeliveriesWidget.DeliveredEvent.class, event -> this.onDeliveredPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.NotDeliveredEvent.class, event -> this.onNotDeliveredPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.CallContactPersonEvent.class, event -> this.onCallContactPersonPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.DidSelectEvent.class, event -> this.onDidSelectDelivery(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.InfoButtonPressedEvent.class, event -> this.onAdditionalInfoButtonPressed(event.getOrder()));
    }

    private void updateUI() {
        loadData();
        deliveriesWidget.setOrders(orders);
    }

    private void updateMapMarker(Order order) {
        if (mapMarkers.containsKey(order.getId())) {
            OLMapMarker marker = mapMarkers.get(order.getId());
            mapComponent.removeMarker(marker);
            mapMarkers.remove(order.getId());
            mapMarkerToOrder.remove(marker.getId());

            OLMapMarker newMarker = createMapMarkerForOrder(order, marker.getCoordinates());
            mapComponent.addMarker(newMarker);
            mapMarkers.put(order.getId(), marker);
            mapMarkerToOrder.put(marker.getId(), order);
        }
    }

    private void onDidSelectDelivery(Order order) {
        if(order == null || !mapMarkers.containsKey(order.getId())) {
            resetMapCenterAndZoom();
            return;
        }

        Point2D coordinates = mapMarkers.get(order.getId()).getCoordinates();
        mapComponent.unlockPosition();
        mapComponent.setCenter(coordinates);
        mapComponent.setZoom(15);
    }

    private void onDeliveredPressed(Order order) {
        orderService.markAsDelivered(order);

        updateMapMarker(order);

        updateUI();

        Notification.show("Menü zugestellt.", 2000, Notification.Position.BOTTOM_START);
    }

    private void onNotDeliveredPressed(Order order) {
        orderService.markAsNotDelivered(order);

        updateMapMarker(order);

        updateUI();

        Notification.show("Menü NICHT zugestellt.", 2000, Notification.Position.BOTTOM_START);
    }

    private void onCallContactPersonPressed(Order order) {
        List<ContactPerson> contactPersons = order.getPerson().getContactPersonFrom();

        if(contactPersons.isEmpty()) {
            Notification.show("Keine Kontaktperson vorhanden!", 2000, Notification.Position.MIDDLE);
            return;
        }

        ContactPerson firstContactPerson_ = contactPersons.get(0);
        Person firstContactPerson = firstContactPerson_.getPerson();

        if(firstContactPerson == null) {
            return;
        }

        Notification.show("Call " + firstContactPerson.getFullName());
    }


    private void onAdditionalInfoButtonPressed(Order order) {
        Dialog dialog = new Dialog();
        AdditionalInformationComponent infoComponent = new AdditionalInformationComponent();
        infoComponent.setFilterType(InformationType.DRIVER);
        infoComponent.setActionColumnVisible(false);
        infoComponent.setPerson(order.getPerson());

        dialog.setWidth(50, Unit.PERCENTAGE);
        dialog.add(infoComponent);

        Button closeButton = new Button("Schließen");
        closeButton.addClickListener(e -> {
            dialog.close();
        });

        dialog.add(new HorizontalLayout(closeButton));

        dialog.open();
    }
}
