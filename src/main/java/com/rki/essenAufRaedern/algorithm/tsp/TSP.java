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

/**
 * @author Thomas Widmann
 * Encapsulates the traveling salesman algorithm.
 * It uses the IRoutingService to resolve the adjacency matrix and the ITSPSolver to calculate the shortest route.
 */
public class TSP {
    private final IRoutingService routingService;
    private final ITspSolver solverService;

    public TSP(IRoutingService routingService, ITspSolver solverService) {
        this.routingService = routingService;
        this.solverService = solverService;
    }

    public TspPathSequence calculateShortestPathSequence(List<Point2D> pointsToVisit, int startingIndex) {
        AdjacencyMatrix matrix = routingService.requestAdjacencyMatrix(pointsToVisit);
        System.out.println("Matrix: ");
        System.out.println(matrix);

        return solverService.solve(matrix, startingIndex);
    }

    public TspPath calculateShortestPath(List<Point2D> pointsToVisit, int startingIndex) {
        TspPathSequence sequence = calculateShortestPathSequence(pointsToVisit, startingIndex);
        List<Point2D> coordinates = sequence.getPath().stream().map(pointsToVisit::get).collect(Collectors.toList());

        return routingService.requestPathBetweenPoints(coordinates);
    }

    public TspPath calculatePathFromCoordinateList(List<Point2D> coordinates) {
        return routingService.requestPathBetweenPoints(coordinates);
    }
}
