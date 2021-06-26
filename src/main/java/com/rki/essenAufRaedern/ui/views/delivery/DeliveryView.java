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
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Fahrer")
@CssImport("./styles/delivery-view.css")
@Route(value = "delivery", layout = MainLayout.class)
@Secured({"DRIVER", "DEVELOPER"})
public class DeliveryView extends VerticalLayout {

    private final OrderDeliveriesWidget deliveriesWidget = new OrderDeliveriesWidget();
    private final OLMap mapComponent = new OLMap();

    private PersonService personService;
    private final OrderService orderService;
    private final KitchenService kitchenService;
    private AddressService addressService;

    private Kitchen kitchen;
    private Point2D kitchenCoordinates;
    private List<Order> orders = new ArrayList<>();
    private TspPath tspRoute;
    private final Map<Long, OLMapMarker> mapOrderIdToMarker = new HashMap<>();
    private final Map<Integer, Order> mapMarkerToOrder = new HashMap<>();
    private final Map<Long, Point2D> mapOrderToCoordinate = new HashMap<>();
    private final Button posSimulationStartButton = new Button("Start");
    private final Button posSimulationPauseButton = new Button("Pause");
    private final Button getPosSimulationResetButton = new Button("Reset");

    private final TSP tsp = new TSP();

    public DeliveryView(KitchenService kitchenService, AddressService addressService, OrderService orderService, PersonService personService) {
        this.kitchenService = kitchenService;
        this.addressService = addressService;
        this.orderService = orderService;
        this.personService = personService;

        addClassName("main-layout");
        setWidthFull();
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

            return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                        && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                        && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
                    && (order.getDelivered() == null
                        && order.getNotDeliverable() == null);
        };

        IRoutingService routingService = RoutingServiceFactory.get().createGraphHopperRoutingService();
        orders = kitchen.getOrders().stream().filter(orderPredicate).collect(Collectors.toList());
        List<Point2D> pointsToVisit = new ArrayList<>();

        kitchenCoordinates = routingService.requestCoordinateFromAddress(kitchen.getAddress().toString());
        pointsToVisit.add(kitchenCoordinates);

        for(Order order : orders) {
            Point2D coordinate = routingService.requestCoordinateFromAddress(order.getPerson().getAddress().toString());
            mapOrderToCoordinate.put(order.getId(), coordinate);
            pointsToVisit.add(coordinate);
        }

        if(pointsToVisit.size() < 2) {
            return;
        }

        System.out.println("Points to visit: " + pointsToVisit);
        List<Point2D> pointsToVisitMinDistance = calculateTSP(pointsToVisit);
        assert(pointsToVisitMinDistance != null);
        System.out.println("Points to visit sorted: " + pointsToVisitMinDistance);

        tspRoute = tsp.calculatePathFromCoordinateList(pointsToVisitMinDistance);
        orders.sort(Comparator.comparingInt(item -> pointsToVisitMinDistance.indexOf(mapOrderToCoordinate.get(item.getId()))));
        Collections.reverse(orders);
    }

    private List<Point2D> calculateTSP(List<Point2D> pointsToVisit) {
        try {
            TspPathSequence tspSequence = tsp.calculateShortestPathSequence(pointsToVisit, 0);

            return tspSequence.getPath().stream().map(pointsToVisit::get).collect(Collectors.toList());
        } catch (Exception exception) {
            return null;
        }
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

        OLMapMarker kitchenMarker = new OLMapMarker(kitchen.getName(), kitchenCoordinates, "house.png");
        mapComponent.addMarker(kitchenMarker);
        resetMapCenterAndZoom();

        // No orders => return;
        if(orders.isEmpty()) {
            return mapComponent;
        }

        for(Order order : orders) {
            Point2D coordinates = mapOrderToCoordinate.get(order.getId());
            OLMapMarker marker = createMapMarkerForOrder(order, coordinates);

            mapComponent.addMarker(marker);
            mapOrderIdToMarker.put(order.getId(), marker);
            mapMarkerToOrder.put(marker.getId(), order);
            System.out.println("Marker: " + marker.getId() + " -> " + order.getPerson().getFullName());
        }

        if(tspRoute != null) {
            OLMapRoute route = new OLMapRoute(tspRoute.getPoints());
            mapComponent.addRoute(route);
            mapComponent.setPositionSimulationRoute(route);
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
        mapComponent.lockPosition();
        mapComponent.setZoom(10);
    }

    private Component createDeliveryListComponent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setMaxWidth(400, Unit.PIXELS);
        layout.setPadding(false);
        layout.setMargin(false);

        deliveriesWidget.setOrders(orders);
        deliveriesWidget.setPadding(false);
        deliveriesWidget.setHeightFull();

        // Simulation button:
        HorizontalLayout simulationButtonsLayout = new HorizontalLayout();
        simulationButtonsLayout.setPadding(true);
        simulationButtonsLayout.setMargin(false);
        simulationButtonsLayout.setWidthFull();
        simulationButtonsLayout.setAlignItems(Alignment.END);
        simulationButtonsLayout.setPadding(false);

        posSimulationPauseButton.setEnabled(false);
        simulationButtonsLayout.add(posSimulationStartButton, posSimulationPauseButton, getPosSimulationResetButton);

        layout.add(deliveriesWidget, simulationButtonsLayout);
        return layout;
    }

    private void addEventListener() {
        // Events of the deliveries component:
        deliveriesWidget.addListener(OrderDeliveriesWidget.DeliveredEvent.class, event -> this.onDeliveredPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.NotDeliveredEvent.class, event -> this.onNotDeliveredPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.CallContactPersonEvent.class, event -> this.onCallContactPersonPressed(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.DidSelectEvent.class, event -> this.onDidSelectDelivery(event.getOrder()));
        deliveriesWidget.addListener(OrderDeliveriesWidget.InfoButtonPressedEvent.class, event -> this.onAdditionalInfoButtonPressed(event.getOrder()));

        // Events for position tracking:
        mapComponent.addMarkerVisitedListener(e -> {
            System.out.println("Visit marker: " + e.getMarkerId());

            if(!mapMarkerToOrder.containsKey(e.getMarkerId())) {
                System.out.println("Order for marker not existing...");
                return;
            }

            Order order = mapMarkerToOrder.get(e.getMarkerId());

            deliveriesWidget.selectOrder(order);
            stopPositionSimulation();
            mapComponent.setZoom(20);
        });

        mapComponent.addMarkerLeaveListener(e -> {
            if(!mapMarkerToOrder.containsKey(e.getMarkerId())) {
                return;
            }

            Order order = mapMarkerToOrder.get(e.getMarkerId());
            deliveriesWidget.deselectOrder(order);
        });

        // Event for the simulation control:
        posSimulationStartButton.addClickListener(e -> {
            startPositionSimulation();
        });

        posSimulationPauseButton.addClickListener(e -> {
            stopPositionSimulation();
        });

        getPosSimulationResetButton.addClickListener(e -> {
            mapComponent.resetPositionSimulation();
        });
    }

    private void updateUI() {
        deliveriesWidget.setOrders(orders);
    }

    private OLMapMarker updateMapMarker(Order order) {
        if (!mapOrderIdToMarker.containsKey(order.getId())) {
            return null;
        }

        OLMapMarker marker = mapOrderIdToMarker.get(order.getId());
        mapComponent.removeMarker(marker);
        mapOrderIdToMarker.remove(order.getId());
        mapMarkerToOrder.remove(marker.getId());

        OLMapMarker newMarker = createMapMarkerForOrder(order, marker.getCoordinates());
        mapComponent.addMarker(newMarker);
        mapOrderIdToMarker.put(order.getId(), newMarker);
        mapMarkerToOrder.put(newMarker.getId(), order);

        return newMarker;
    }

    private void onDidSelectDelivery(Order order) {
        if(order == null || !mapOrderIdToMarker.containsKey(order.getId())) {
            resetMapCenterAndZoom();
            return;
        }

        Point2D coordinates = mapOrderIdToMarker.get(order.getId()).getCoordinates();
        mapComponent.unlockPosition();
        mapComponent.setCenter(coordinates);
        mapComponent.setZoom(15);
    }

    private void onDeliveredPressed(Order order) {
        orderService.markAsDelivered(order);

        OLMapMarker marker = updateMapMarker(order);
        if(marker == null) {
            return;
        }

        mapMarkerToOrder.remove(marker.getId());
        orders.remove(order);
        updateUI();

        Notification.show("Menü zugestellt.", 2000, Notification.Position.BOTTOM_START);

        startPositionSimulation();
    }

    private void onNotDeliveredPressed(Order order) {
        orderService.markAsNotDelivered(order);

        OLMapMarker marker = updateMapMarker(order);
        if(marker == null) {
            return;
        }

        mapMarkerToOrder.remove(marker.getId());
        orders.remove(order);
        updateUI();

        Notification.show("Menü NICHT zugestellt.", 2000, Notification.Position.BOTTOM_START);

        startPositionSimulation();
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

    // Simulation:
    private void startPositionSimulation() {
        posSimulationStartButton.setEnabled(false);
        posSimulationPauseButton.setEnabled(true);

        mapComponent.startPositionSimulation();
    }

    private void stopPositionSimulation() {
        posSimulationPauseButton.setEnabled(true);
        posSimulationStartButton.setEnabled(false);

        mapComponent.stopPositionSimulation();
    }
}
