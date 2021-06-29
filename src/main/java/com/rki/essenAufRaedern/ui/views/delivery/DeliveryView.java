package com.rki.essenAufRaedern.ui.views.delivery;

import com.rki.essenAufRaedern.algorithm.tsp.TSP;
import com.rki.essenAufRaedern.algorithm.tsp.api.IRoutingService;
import com.rki.essenAufRaedern.algorithm.tsp.api.RoutingServiceFactory;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPath;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;
import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.service.KitchenService;
import com.rki.essenAufRaedern.backend.service.OrderService;
import com.rki.essenAufRaedern.backend.service.UserService;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.olmap.OLMap;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapMarker;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapRoute;
import com.rki.essenAufRaedern.ui.components.orders.OrderDeliveriesWidget;
import com.rki.essenAufRaedern.ui.components.person.AdditionalInformationComponent;
import com.rki.essenAufRaedern.ui.components.person.ContactPersonComponent;
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
import org.springframework.security.access.annotation.Secured;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@PageTitle("Fahrer")
@CssImport("./styles/delivery-view.css")
@Route(value = "delivery", layout = MainLayout.class)
@Secured({"DRIVER", "DEVELOPER"})
public class DeliveryView extends VerticalLayout {

    // Components:
    private final OrderDeliveriesWidget deliveriesList = new OrderDeliveriesWidget();
    private final OLMap mapComponent = new OLMap();
    private final Button posSimulationStartButton = new Button("Start");
    private final Button posSimulationPauseButton = new Button("Pause");
    private final Button posSimulationResetButton = new Button("Reset");

    // Services:
    private final OrderService orderService;
    private final KitchenService kitchenService;
    private final UserService userService;

    // Variables:
    private Kitchen kitchen;
    private Point2D kitchenCoordinates;
    private List<Order> orders = new ArrayList<>();
    private final Map<Long, OLMapMarker> mapOrderIdToMarker = new HashMap<>();
    private final Map<Integer, Order> mapMarkerToOrder = new HashMap<>();
    private final Map<Long, Point2D> mapOrderToCoordinate = new HashMap<>();

    private final IRoutingService routingService;
    private final TSP tsp = new TSP();
    private final TspPath tspRoute;

    public DeliveryView(KitchenService kitchenService, OrderService orderService, UserService userService) {
        this.kitchenService = kitchenService;
        this.orderService = orderService;
        this.userService = userService;

        addClassName("main-layout");
        setWidthFull();
        setPadding(false);

        routingService = RoutingServiceFactory.get().createGraphHopperRoutingService();

        loadDataFromDatabase();
        resolveAddresses();

        List<Point2D> pointsToVisitMinDistance = calculatePathForPointsToVisit();
        tspRoute = calculateRouteFromPoints(pointsToVisitMinDistance);
        sortOrdersByPointsToVisit(pointsToVisitMinDistance);

        add(createMainLayout());
        addEventListener();
    }

    private void loadDataFromDatabase() {
        kitchen = getKitchenFromCurrentUser();

        if(kitchen == null) {
            return;
        }

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

        orders = kitchen.getOrders().stream().filter(orderPredicate).collect(Collectors.toList());
    }

    private Kitchen getKitchenFromCurrentUser() {
        User currentUser = userService.getCurrentUser();
        Person currentPerson = currentUser.getPerson();
        Employee currentEmployee = currentPerson.getEmployee();
        if(currentEmployee == null) {
            return null;
        }

        return currentEmployee.getKitchen();
    }

    private void sortOrdersByPointsToVisit(List<Point2D> pointsToVisitMinDistance) {
        orders.sort(Comparator.comparingInt(item -> pointsToVisitMinDistance.indexOf(mapOrderToCoordinate.get(item.getId()))));
        Collections.reverse(orders);
    }

    private TspPath calculateRouteFromPoints(List<Point2D> pointsToVisitMinDistance) {
        if(pointsToVisitMinDistance.isEmpty()) {
            return new TspPath();
        }
        return tsp.calculatePathFromCoordinateList(pointsToVisitMinDistance);
    }

    private List<Point2D> calculatePathForPointsToVisit() {
        List<Point2D> pointsToVisit = createPointsToVisitList();

        if(pointsToVisit.size() < 2) {
            return new ArrayList<>();
        }

        try {
            TspPathSequence tspSequence = tsp.calculateShortestPathSequence(pointsToVisit, 0);
            return tspSequence.getPath().stream().map(pointsToVisit::get).collect(Collectors.toList());
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }

    private void resolveAddresses() {
        if(kitchen == null) {
            return;
        }

        kitchenCoordinates = routingService.requestCoordinateFromAddress(kitchen.getAddress().toString());
        for(Order order : orders) {
            Point2D coordinate = routingService.requestCoordinateFromAddress(order.getPerson().getAddress().toString());
            mapOrderToCoordinate.put(order.getId(), coordinate);
        }
    }

    private List<Point2D> createPointsToVisitList() {
        List<Point2D> pointsToVisit = new ArrayList<>();
        pointsToVisit.add(kitchenCoordinates);

        mapOrderToCoordinate.forEach((order, coordinate) -> {
            pointsToVisit.add(coordinate);
        });

        return pointsToVisit;
    }

    private Component createMainLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setHeightFull();
        layout.add(createMapView(), createDeliveryListComponent());

        return layout;
    }

    private Component createDeliveryListComponent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setMaxWidth(400, Unit.PIXELS);
        layout.setPadding(false);
        layout.setMargin(false);

        deliveriesList.setOrders(orders);
        deliveriesList.setPadding(false);
        deliveriesList.setHeightFull();

        // Simulation button:
        HorizontalLayout simulationButtonsLayout = new HorizontalLayout();
        simulationButtonsLayout.setPadding(true);
        simulationButtonsLayout.setMargin(false);
        simulationButtonsLayout.setWidthFull();
        simulationButtonsLayout.setAlignItems(Alignment.END);
        simulationButtonsLayout.setPadding(false);

        posSimulationPauseButton.setEnabled(false);
        simulationButtonsLayout.add(posSimulationStartButton, posSimulationPauseButton, posSimulationResetButton);

        layout.add(deliveriesList, simulationButtonsLayout);
        return layout;
    }

    private Component createMapView() {
        mapComponent.setHeight(100, Unit.PERCENTAGE);
        mapComponent.setWidthFull();

        if(kitchen != null) {
            OLMapMarker kitchenMarker = new OLMapMarker(kitchen.getName(), kitchenCoordinates, "house.png");
            mapComponent.addMarker(kitchenMarker);
        }

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

    private void addEventListener() {
        // Events of the deliveries component:
        deliveriesList.addListener(OrderDeliveriesWidget.DeliveredEvent.class, event -> this.onDeliveredPressed(event.getOrder()));
        deliveriesList.addListener(OrderDeliveriesWidget.NotDeliveredEvent.class, event -> this.onNotDeliveredPressed(event.getOrder()));
        deliveriesList.addListener(OrderDeliveriesWidget.CallContactPersonEvent.class, event -> this.onCallContactPersonPressed(event.getOrder()));
        deliveriesList.addListener(OrderDeliveriesWidget.DidSelectEvent.class, event -> this.onDidSelectDelivery(event.getOrder()));
        deliveriesList.addListener(OrderDeliveriesWidget.InfoButtonPressedEvent.class, event -> this.onAdditionalInfoButtonPressed(event.getOrder()));

        // Events for position tracking:
        mapComponent.addMarkerVisitedListener(this::onVisitedPersonsAddress);
        mapComponent.addMarkerLeaveListener(this::onLeavePersonsAddress);

        // Event for the simulation control:
        posSimulationStartButton.addClickListener(e -> startPositionSimulation());
        posSimulationPauseButton.addClickListener(e -> stopPositionSimulation());
        posSimulationResetButton.addClickListener(e -> mapComponent.resetPositionSimulation());
    }

    private void updateDeliveriesList() {
        deliveriesList.setOrders(orders);
    }

    private OLMapMarker updateMapMarkerStatus(Order order) {
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

        OLMapMarker marker = updateMapMarkerStatus(order);
        if(marker == null) {
            return;
        }

        mapMarkerToOrder.remove(marker.getId());
        orders.remove(order);
        updateDeliveriesList();

        Notification.show("Menü zugestellt.", 2000, Notification.Position.BOTTOM_START);

        startPositionSimulation();
    }

    private void onNotDeliveredPressed(Order order) {
        orderService.markAsNotDelivered(order);

        OLMapMarker marker = updateMapMarkerStatus(order);
        if(marker == null) {
            return;
        }

        mapMarkerToOrder.remove(marker.getId());
        orders.remove(order);
        updateDeliveriesList();

        Notification.show("Menü NICHT zugestellt.", 2000, Notification.Position.BOTTOM_START);

        startPositionSimulation();
    }

    private void onCallContactPersonPressed(Order order) {
        if(!order.getPerson().hasContactPersons()) {
            Notification.show("Keine Kontaktperson vorhanden!", 2000, Notification.Position.MIDDLE);
            return;
        }

        showContactPersonDialogForOrder(order);
    }

    private void onAdditionalInfoButtonPressed(Order order) {
        showAdditionalInformationDialogForOrder(order);
    }

    private void onLeavePersonsAddress(OLMap.MarkerLeaveEvent e) {
        if(!mapMarkerToOrder.containsKey(e.getMarkerId())) {
            return;
        }

        Order order = mapMarkerToOrder.get(e.getMarkerId());
        deliveriesList.deselectOrder(order);
    }

    private void onVisitedPersonsAddress(OLMap.MarkerVisitedEvent e) {
        if(!mapMarkerToOrder.containsKey(e.getMarkerId())) {
            System.out.println("Order for marker not existing...");
            return;
        }

        Order order = mapMarkerToOrder.get(e.getMarkerId());

        deliveriesList.selectOrder(order);
        stopPositionSimulation();
        mapComponent.setZoom(20);
    }

    private void showContactPersonDialogForOrder(Order order) {
        Dialog contactPersonDialog = new Dialog();
        contactPersonDialog.setWidth(50, Unit.PERCENTAGE);
        ContactPersonComponent contactPersonList = new ContactPersonComponent(new ContactPersonComponent.Config().allowCall(true).allowDelete(false));
        contactPersonList.setPerson(order.getPerson());
        contactPersonList.addListener(ContactPersonComponent.CallButtonPressedEvent.class, e -> { Notification.show("Call " + e.getContactPerson().getContactPersonFrom().getFullName());});
        contactPersonDialog.add(contactPersonList);
        Button closeButton = new Button("Schließen");
        closeButton.addClickListener(e -> contactPersonDialog.close());
        contactPersonDialog.open();
    }

    private void showAdditionalInformationDialogForOrder(Order order) {
        Dialog dialog = new Dialog();
        AdditionalInformationComponent infoComponent = new AdditionalInformationComponent();
        infoComponent.setFilterType(InformationType.DRIVER);
        infoComponent.setActionColumnVisible(false);
        infoComponent.setPerson(order.getPerson());
        dialog.setWidth(50, Unit.PERCENTAGE);
        dialog.add(infoComponent);
        Button closeButton = new Button("Schließen");
        closeButton.addClickListener(e -> dialog.close());
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
