package com.rki.essenAufRaedern.ui.views.delivery;

import com.rki.essenAufRaedern.algorithm.tsp.TSP;
import com.rki.essenAufRaedern.algorithm.tsp.api.IRoutingService;
import com.rki.essenAufRaedern.algorithm.tsp.api.RoutingServiceFactory;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPath;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;
import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Kitchen;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.service.OrderService;
import com.rki.essenAufRaedern.backend.service.PersonService;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.rki.essenAufRaedern.ui.components.olmap.OLMap;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapMarker;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapRoute;
import com.rki.essenAufRaedern.ui.components.orders.OrderDeliveriesWidget;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageTitle("Delivery")
@Route(value = "delivery", layout = MainLayout.class)
public class DeliveryView extends VerticalLayout {

    private OrderDeliveriesWidget deliveriesWidget = new OrderDeliveriesWidget();
    private OLMap mapComponent = new OLMap();

    private PersonService personService;
    private OrderService orderService;

    private Kitchen kitchen;
    private Map<Order, OLMapMarker> mapMarkers = new HashMap<>();
    private OLMapMarker kitchenMarker;

    public DeliveryView() {
        createDummyData();

        add(createMainLayout());

        addEventListener();
    }

    private Component createMainLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setHeightFull();
        layout.add(createMapView(), createDeliveryListComponent());

        return layout;
    }

    private Component createMapView() {
        mapComponent.setHeight(500, Unit.PIXELS);
        mapComponent.setWidthFull();

        List<Point2D> pointsToVisit = new ArrayList<>();

        IRoutingService routingService = RoutingServiceFactory.get().createGraphHopperRoutingService();

        Point2D kitchenCoordinates = routingService.requestCoordinateFromAddress(kitchen.getAddress().toString());
        kitchenMarker = new OLMapMarker(kitchen.getName(), kitchenCoordinates);
        mapComponent.addMarker(kitchenMarker);

        pointsToVisit.add(kitchenCoordinates);

        for(Order order : kitchen.getOrders()) {
            Person person = order.getPerson();
            Address address = person.getAddress();
            String addressString = address.toString();
            String personString = person.getFirstName() + " " + person.getLastName();
            Point2D coordinates = routingService.requestCoordinateFromAddress(addressString);
            OLMapMarker marker = new OLMapMarker(personString, coordinates);

            mapComponent.addMarker(marker);
            mapMarkers.put(order, marker);
            pointsToVisit.add(coordinates);
        }

        TSP tsp = new TSP();
        TspPathSequence tspSequence = tsp.calculateShortestPathSequence(pointsToVisit, 0);
        List<Point2D> tspSequenceCoordinates = tspSequence.getPath().stream().map(pointsToVisit::get).collect(Collectors.toList());
        TspPath tspRoute = tsp.calculatePathFromCoordinateList(tspSequenceCoordinates);
        OLMapRoute route = new OLMapRoute(tspRoute.getPoints());

        mapComponent.addRoute(route);
        return mapComponent;
    }

    private Component createDeliveryListComponent() {
        deliveriesWidget.setOrders(kitchen.getOrders());
        deliveriesWidget.setWidthFull();
        deliveriesWidget.setMaxWidth(400, Unit.PIXELS);
        return deliveriesWidget;
    }

    private void addEventListener() {
        deliveriesWidget.addListener(OrderDeliveriesWidget.StartEvent.class, event -> this.onStartDelivery(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.EndEvent.class, event -> this.onEndDelivery(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.DidSelectEvent.class, event -> this.onDidSelectDelivery(event.getOrder()));
    }

    private void onDidSelectDelivery(Order order) {
        if(!mapMarkers.containsKey(order)) {
            return;
        }

        Point2D coordinates = mapMarkers.get(order).getCoordinates();
        mapComponent.setCenter(coordinates);
        mapComponent.setZoom(15);
    }

    private void onStartDelivery(Order order) {
        System.out.println("Started delivery of: " + order);
    }

    private void onEndDelivery(Order order) {
        System.out.println("Finished delivery of: " + order);
    }

    private void createDummyData() {
        Person oPerson1 = new Person();
        oPerson1.setFirstName("Max");
        oPerson1.setLastName("Mustermann");

        Address address1 = new Address();
        address1.setCity("Innsbruck");
        address1.setHouseNumber("26");
        address1.setStreet("Anichstraße");
        address1.setZipCode("6020");

        Address address2 = new Address();
        address2.setCity("Innsbruck");
        address2.setHouseNumber("7");
        address2.setStreet("Schießstandgasse");
        address2.setZipCode("6020");

        oPerson1.setAddress(address1);

        Person oPerson2 = new Person();
        oPerson2.setFirstName("Anna");
        oPerson2.setLastName("Mustermann");

        oPerson2.setAddress(address2);

        Order oOrder1 = new Order();
        oPerson1.addOrder(oOrder1);

        Order order2 = new Order();
        oPerson2.addOrder(order2);

        Address kitchenAddress = new Address();
        kitchenAddress.setCity("Innsbruck");
        kitchenAddress.setHouseNumber("123");
        kitchenAddress.setStreet("Tiergartenstraße");
        kitchenAddress.setZipCode("6020");

        kitchen = new Kitchen();
        kitchen.setAddress(kitchenAddress);
        kitchen.setName("Küche");
        kitchen.addOrder(oOrder1);
        kitchen.addOrder(order2);
    }
}
