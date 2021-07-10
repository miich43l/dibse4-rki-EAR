package com.rki.essenAufRaedern.algorithm.tsp.service;


import com.rki.essenAufRaedern.algorithm.tsp.api.IRoutingService;
import com.rki.essenAufRaedern.algorithm.tsp.api.RoutingServiceFactory;
import com.rki.essenAufRaedern.algorithm.tsp.solver.ITspSolver;
import com.rki.essenAufRaedern.algorithm.tsp.solver.TspSolverFactory;
import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPath;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;
import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Order;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Thomas Widmann
 * Provides a service for the traveling salesman algorithm.
 * It uses the IRoutingService to resolve the adjacency matrix and the ITSPSolver to calculate the shortest route.
 */

@Service
public class TSPService {

    private final IRoutingService routingService;
    private final ITspSolver tspSolverService;

    public TSPService() {
        routingService = RoutingServiceFactory.get().createGraphHopperRoutingService();
        tspSolverService = TspSolverFactory.get().createBacktrackSolver();
    }

    public Point2D resolveAddress(Address address) {
        return routingService.requestCoordinateFromAddress(address.toString());
    }

    public TspPath calculateRouteBetweenPoints(List<Point2D> points) {
        return routingService.requestPathBetweenPoints(points);
    }

    public List<Point2D> calculateShortestPathSequenceBetweenPoints(List<Point2D> pointsToVisit, int startPoint) {
        if(pointsToVisit.size() < 2) {
            return new ArrayList<>();
        }

        AdjacencyMatrix adjacencyMatrix = routingService.requestAdjacencyMatrix(pointsToVisit);

        try {
            TspPathSequence tspPathSequence = tspSolverService.solve(adjacencyMatrix, startPoint);
            return tspPathSequence.getPath().stream().map(pointsToVisit::get).collect(Collectors.toList());
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }
}
