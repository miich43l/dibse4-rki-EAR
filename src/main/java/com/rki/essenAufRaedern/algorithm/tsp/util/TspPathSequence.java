package com.rki.essenAufRaedern.algorithm.tsp.util;

import java.util.List;

/**
 * @author Thomas Widmann
 * Represents the sequence of the path (Point1, Point3, Point5, Point4, Point2) and the corresponding cost.
 */
public class TspPathSequence {

    public TspPathSequence(double cost, List<Integer> path) {
        this.cost = cost;
        this.path = path;
    }

    public List<Integer> getPath() {
        return path;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "TspPathSequence{" +
                "path=" + path +
                '}';
    }

    private final double cost;
    private final List<Integer> path;
}
