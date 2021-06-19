package com.rki.essenAufRaedern.ui.components.olmap;

import com.google.gson.Gson;

import java.awt.geom.Point2D;

public class OLMapMarker {
    private transient OLMap map;
    private String title;
    private Point2D.Double coordinates = new Point2D.Double();
    private int id;

    public OLMapMarker(String title, Point2D coordinates) {
        this.title = title;
        this.coordinates.setLocation(coordinates);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Point2D getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point2D coordinates) {
        this.coordinates.setLocation(coordinates);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
