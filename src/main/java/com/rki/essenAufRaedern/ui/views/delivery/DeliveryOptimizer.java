package com.rki.essenAufRaedern.ui.views.delivery;

import com.rki.essenAufRaedern.algorithm.tsp.service.TSPService;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPath;
import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Order;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryOptimizer {
    private final Map<Long, Point2D> mapOrderIdToCoordinate = new HashMap<>();
    private Point2D kitchenCoordinate;
    private final TSPService service;
    private TspPath tspPath;
    private List<Point2D> optimizedDeliverySequence;

    // -- public:

    public DeliveryOptimizer(TSPService service) {
        this.service = service;
    }

    public void optimizeDeliveries(List<Order> orders, Address kitchenAddress) {
        resolveOrdersAndKitchenAddresses(orders, kitchenAddress);
        optimizedDeliverySequence = calculateShortestPathForPointsToVisit();
        tspPath = calculateRouteFromPoints(optimizedDeliverySequence);
    }

    public Map<Long, Point2D> getMapOrderIdToCoordinate() {
        return mapOrderIdToCoordinate;
    }

    public Point2D getKitchenCoordinate() {
        return kitchenCoordinate;
    }

    public TspPath getTspPath() {
        return tspPath;
    }

    public List<Point2D> getOptimizedDeliverySequence() {
        return optimizedDeliverySequence;
    }

    // -- private:

    private TspPath calculateRouteFromPoints(List<Point2D> pointsToVisitMinDistance) {
        if(pointsToVisitMinDistance.isEmpty()) {
            return new TspPath();
        }

        return service.calculateRouteBetweenPoints(pointsToVisitMinDistance);
    }

    private void resolveOrdersAndKitchenAddresses(List<Order> orders, Address kitchenAddress) {
        kitchenCoordinate = service.resolveAddress(kitchenAddress);
        for(Order order : orders) {
            Address address = order.getPerson().getAddress();
            Point2D coordinate = service.resolveAddress(address);
            mapOrderIdToCoordinate.put(order.getId(), coordinate);
        }
    }

    private List<Point2D> calculateShortestPathForPointsToVisit() {
        List<Point2D> pointsToVisit = createPointsToVisitList();
        List<Point2D> pointsToVisitOptimized = service.calculateShortestPathSequenceBetweenPoints(pointsToVisit, 0);

        System.out.println("==> " + pointsToVisit);
        System.out.println("==> " + pointsToVisitOptimized);

        return pointsToVisitOptimized;
    }

    private List<Point2D> createPointsToVisitList() {
        List<Point2D> pointsToVisit = new ArrayList<>();
        pointsToVisit.add(kitchenCoordinate);

        mapOrderIdToCoordinate.forEach((order, coordinate) -> {
            pointsToVisit.add(coordinate);
        });

        return pointsToVisit;
    }
}
