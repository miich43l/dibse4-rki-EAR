package com.rki.essenAufRaedern.algorithm.tsp.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Widmann
 * Represents a path between points.
 */
public class TspPath {

    public TspPath() {
        this.path = new ArrayList<>();
    }

    public TspPath(List<Point2D> path) {
        this.path = path;
    }

    public List<Point2D> getPoints() {
        return path;
    }

    public void setPath(List<Point2D> path) {
        this.path = path;
    }

    public void addPoint(Point2D point) {
        path.add(path.size(), point);
    }

    private List<Point2D> path;
}
