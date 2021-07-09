package com.rki.essenAufRaedern.ui.components.olmap;

import com.google.gson.Gson;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OLMapRoute {
    private transient OLMap map;
    private List<Point2D.Double> points = new ArrayList<>();
    private int id;

    public OLMapRoute() {
    }

    public OLMapRoute(List<Point2D> points) {
        setPoints(points);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public OLMap getMap() {
        return map;
    }

    public void setMap(OLMap map) {
        this.map = map;
    }

    public List<Point2D.Double> getPoints() {
        return points;
    }

    public void setPoints(List<Point2D> points) {
        this.points = points.stream().map(item -> {Point2D.Double p = new Point2D.Double(); p.setLocation(item); return p;}).collect(Collectors.toList());
    }

    public void addPoint(Point2D point) {
        Point2D.Double _point = new Point2D.Double();
        _point.setLocation(point);
        this.points.add(this.points.size(), _point);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
