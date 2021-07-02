package com.rki.essenAufRaedern.algorithm.tsp.api;

import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPath;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * @author Thomas Widmann
 * Routing service interface.
 */
public interface IRoutingService {
    AdjacencyMatrix requestAdjacencyMatrix(List<Point2D> lstPoints);
    TspPath requestPathBetweenPoints(List<Point2D> coordinates);
    Point2D requestCoordinateFromAddress(String address);
}
