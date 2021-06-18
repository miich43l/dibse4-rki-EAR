package com.rki.essenAufRaedern.ui.components.olmap;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.SerializableConsumer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Tag("openlayers")
@NpmPackage(value = "ol", version = "6.1.1")
@CssImport("ol/ol.css")
//@JavaScript("https://cdn.jsdelivr.net/gh/openlayers/openlayers.github.io@master/en/v6.1.1/build/ol.js")
//@StyleSheet("https://cdn.jsdelivr.net/gh/openlayers/openlayers.github.io@master/en/v6.1.1/css/ol.css")
@JsModule("./src/openlayers-connector.js")
public class OLMap extends Div {

    int nextLayerId = 0;
    List<OLMapMarker> markers = new ArrayList<>();
    List<OLMapRoute> routes = new ArrayList<>();

    public OLMap() {
        initConnector();
    }

    public void addMarker(OLMapMarker marker) {
        marker.setMap(this);
        marker.setId(nextLayerId);
        markers.add(markers.size(), marker);

        getElement().callJsFunction("addMarker", marker.toJson());
        nextLayerId++;
    }

    public void addMarkers(List<OLMapMarker> markers) {
        for(OLMapMarker marker : markers) {
            addMarker(marker);
        }
    }

    public void removeMarker(OLMapMarker marker) {
        if(!markers.contains(marker)) {
            return;
        }

        getElement().callJsFunction("removeMarker", marker.toJson());
        marker.setMap(null);
        marker.setId(-1);
        markers.remove(marker);
    }

    public void addRoute(OLMapRoute route) {
        route.setMap(this);
        route.setId(nextLayerId);
        routes.add(routes.size(), route);

        getElement().callJsFunction("addRoute", route.toJson());
        nextLayerId++;
    }

    public void removeRoute(OLMapRoute route) {
        if(!routes.contains(route)) {
            return;
        }

        getElement().callJsFunction("removeRoute", route.toJson());
        route.setMap(null);
        route.setId(-1);
        routes.remove(route);
    }

    public void removeAllRoutes() {
        routes.forEach(this::removeRoute);
    }

    public void setZoom(int zoom) {
        getElement().callJsFunction("setZoom", zoom);
    }

    public void setCenter(Point2D center) {
        getElement().callJsFunction("setCenter", center.getX(), center.getY());
    }

    public List<OLMapMarker> getMarkers() {
        return markers;
    }

    private void initConnector() {
        runBeforeClientResponse(ui -> ui.getPage().executeJs("window.Vaadin.Flow.openLayersConnector.initLazy($0)", getElement()));
    }

    private void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context -> command.accept(ui)));
    }
}
