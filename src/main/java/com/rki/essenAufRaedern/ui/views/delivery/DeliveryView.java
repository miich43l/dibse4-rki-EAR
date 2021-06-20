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
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.person.AdditionalInformationComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
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
import java.util.*;
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
    private Map<Order, OLMapMarker> mapMarkers = new HashMap<>();
    private OLMapMarker kitchenMarker;

    public DeliveryView(KitchenService kitchenService, AddressService addressService) {
        this.kitchenService = kitchenService;
        this.addressService = addressService;

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
        System.out.println("Load data...");
        List<Kitchen> kitchens = kitchenService.findAll();
        System.out.println("Kitchens: " + kitchens);

        this.kitchen = kitchens.get(0);

        System.out.println(" ==> all addresses: " + addressService.findAll());

        System.out.println("Kitchen add: " + kitchen.getAddress());
    }

    private Component createMainLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setHeightFull();
        //layout.add(createMapView());
        layout.add(createMapView(), createDeliveryListComponent());

        return layout;
    }

    private Component createMapView() {
        mapComponent.setHeight(100, Unit.PERCENTAGE);
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
        deliveriesWidget.setPadding(false);

        return deliveriesWidget;
    }

    private void addEventListener() {
        deliveriesWidget.addListener(OrderDeliveriesWidget.DeliveredEvent.class, event -> this.onDeliveredPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.NotDeliveredEvent.class, event -> this.onNotDeliveredPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.CallContactPersonEvent.class, event -> this.onCallContactPersonPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.DidSelectEvent.class, event -> this.onDidSelectDelivery(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.InfoButtonPressedEvent.class, event -> this.onAdditionalInfoButtonPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.DidSelectEvent.class, event -> this.onDidSelectDelivery(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.InfoButtonPressedEvent.class, event -> this.onAdditionalInfoButtonPressed(event.getOrder()));
    }

    private void onDidSelectDelivery(Order order) {
        if(!mapMarkers.containsKey(order)) {
            return;
        }

        Point2D coordinates = mapMarkers.get(order).getCoordinates();
        mapComponent.setCenter(coordinates);
        mapComponent.setZoom(15);
    }

    private void onDeliveredPressed(Order order) {
        order.setDelivered(new Timestamp(new Date().getTime()));
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

        ContactPerson firstContactPerson = contactPersons.get(0);
        //TODO: Get phonenumber...

        Notification.show("Not supported yet! :-(", 2000, Notification.Position.MIDDLE);
    }


    private void onAdditionalInfoButtonPressed(Order order) {
        Dialog dialog = new Dialog();
        AdditionalInformationComponent infoComponent = new AdditionalInformationComponent();
        infoComponent.setPerson(order.getPerson());

        dialog.setWidth(50, Unit.PERCENTAGE);
        dialog.setHeight("70vh");
        dialog.add(infoComponent);
        dialog.open();
    }


    private void createDummyData() {
        Person oPerson1 = new Person();
        oPerson1.setFirstName("Max");
        oPerson1.setLastName("Mustermann");

        AdditionalInformation info1 = new AdditionalInformation();
        info1.setValue("Schlüssel links neben der Tür.");
        AdditionalInformation info2 = new AdditionalInformation();
        info2.setValue("Beim Fenster klopfen.");
        oPerson1.addAdditionalInformation(info1);
        oPerson1.addAdditionalInformation(info2);


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

        Person oPerson3 = new Person();
        oPerson3.setFirstName("Rosi");
        oPerson3.setLastName("Hauser");

        Address address3 = new Address();
        address3.setCity("Innsbruck");
        address3.setHouseNumber("60");
        address3.setStreet("Schneeburggasse");
        address3.setZipCode("6020");

        oPerson3.setAddress(address3);

        Order oOrder1 = new Order();
        oPerson1.addOrder(oOrder1);

        Order order2 = new Order();
        oPerson2.addOrder(order2);

        Order order3 = new Order();
        oPerson3.addOrder(order3);

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
        kitchen.addOrder(order3);
    }
}
