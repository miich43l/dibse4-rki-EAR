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
import com.rki.essenAufRaedern.backend.utility.Status;
import com.rki.essenAufRaedern.ui.MainLayout;
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
import com.rki.essenAufRaedern.ui.components.olmap.OLMap;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapMarker;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapRoute;
import com.rki.essenAufRaedern.ui.components.orders.OrderDeliveriesWidget;
import org.springframework.context.annotation.Scope;

import java.awt.geom.Point2D;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private Map<Order, OLMapMarker> mapMarkers = new HashMap<>();
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
                    && order.getStatus() == Status.Active;
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
            mapMarkers.put(order, marker);
            pointsToVisit.add(coordinates);
        }

        try {
            TSP tsp = new TSP();
            TspPathSequence tspSequence = tsp.calculateShortestPathSequence(pointsToVisit, 0);
            List<Point2D> tspSequenceCoordinates = tspSequence.getPath().stream().map(pointsToVisit::get).collect(Collectors.toList());
            TspPath tspRoute = tsp.calculatePathFromCoordinateList(tspSequenceCoordinates);
            OLMapRoute route = new OLMapRoute(tspRoute.getPoints());

            mapComponent.addRoute(route);
        } catch (Exception exception) {
            Notification.show("Failed to calculate TSP!");
        }

        return mapComponent;
    }

    private OLMapMarker createMapMarkerForOrder(Order order, Point2D coordinates) {
        Person person = order.getPerson();
        String strIcons = "marker_red.png";

        if(order.getDelivered() != null) {
            strIcons = "marker_gray.png";
        }

        return new OLMapMarker(person.getFullName(), coordinates, strIcons);
    }

    private void resetMapCenterAndZoom() {
        if(kitchenMarker == null) {
            return;
        }

        mapComponent.setCenter(kitchenMarker.getCoordinates());
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

    private void onDidSelectDelivery(Order order) {
        if(order == null || !mapMarkers.containsKey(order)) {
            resetMapCenterAndZoom();
            return;
        }

        Point2D coordinates = mapMarkers.get(order).getCoordinates();
        mapComponent.setCenter(coordinates);
        mapComponent.setZoom(15);
    }

    private void onDeliveredPressed(Order order) {
        orderService.markAsDelivered(order);

        if(mapMarkers.containsKey(order)) {
            OLMapMarker marker = mapMarkers.get(order);
            mapComponent.removeMarker(marker);

            OLMapMarker newMarker = createMapMarkerForOrder(order, marker.getCoordinates());
            mapComponent.addMarker(newMarker);
        }

        updateUI();

        Notification.show("Menü zugestellt.", 2000, Notification.Position.BOTTOM_START);
    }

    private void onNotDeliveredPressed(Order order) {
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
        infoComponent.setFilterType(InformationType.Driver);
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
