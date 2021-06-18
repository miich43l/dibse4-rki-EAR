package com.rki.essenAufRaedern.algorithm.tsp;

import com.rki.essenAufRaedern.algorithm.tsp.api.IRoutingService;
import com.rki.essenAufRaedern.algorithm.tsp.api.RoutingServiceFactory;
import com.rki.essenAufRaedern.algorithm.tsp.solver.ITspSolver;
import com.rki.essenAufRaedern.algorithm.tsp.solver.TspSolverFactory;
import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPath;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;

public class TSP {
    public TSP() {
        this.routingService = RoutingServiceFactory.get().createGraphHopperRoutingService();
        this.solverService = TspSolverFactory.get().createDefaultSolver();
    }

    public TspPathSequence calculateShortestPathSequence(List<Point2D> pointsToVisit, int startingIndex) {
        AdjacencyMatrix matrix = routingService.requestAdjacencyMatrix(pointsToVisit);
        System.out.println("Matrix: ");
        System.out.println(matrix);
        this.shortestPath = solverService.solve(matrix, startingIndex);

        return this.shortestPath;
    }

    public TspPath calculateShortestPath(List<Point2D> pointsToVisit, int startingIndex) {
        TspPathSequence sequence = calculateShortestPathSequence(pointsToVisit, startingIndex);
        List<Point2D> coordinates = sequence.getPath().stream().map(pointsToVisit::get).collect(Collectors.toList());

        return routingService.requestPathBetweenPoints(coordinates);
    }

    public TspPath calculatePathFromCoordinateList(List<Point2D> coordinates) {
        return routingService.requestPathBetweenPoints(coordinates);
    }

    private IRoutingService routingService;
    private ITspSolver solverService;
    private TspPathSequence shortestPath;
}
