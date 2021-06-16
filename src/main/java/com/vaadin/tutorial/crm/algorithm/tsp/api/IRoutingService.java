package com.vaadin.tutorial.crm.algorithm.tsp.api;

import com.vaadin.tutorial.crm.algorithm.tsp.util.AdjacencyMatrix;
import com.vaadin.tutorial.crm.algorithm.tsp.util.TspPath;

import java.awt.geom.Point2D;
import java.util.List;

public interface IRoutingService {
    AdjacencyMatrix requestAdjacencyMatrix(List<Point2D> lstPoints);
    TspPath requestPathBetweenPoints(List<Point2D> coordinates);
    Point2D requestCoordinateFromAddress(String address);
}
