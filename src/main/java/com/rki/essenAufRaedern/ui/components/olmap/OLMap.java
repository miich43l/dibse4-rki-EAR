package com.rki.essenAufRaedern.ui.components.olmap;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Widmann
 * The OLMap Component allows to use a openlayer map in the application.
 * It uses a JavaScript connector for communication between the client and the server.
 *
 * It is possible to add markers (OLMapMarker) or routes (OLMapRoute).
 *
 * You can also add some POIs (Points of interesst).
 * When the user visits a POI, an event is sent.
 */

@Tag("openlayers")
@NpmPackage(value = "ol", version = "6.1.1")
@CssImport("ol/ol.css")
@JsModule("./src/openlayers-connector.js")
public class OLMap extends Div {
    private int nextLayerId = 0;
    private boolean positionSimulationActive = false;
    private OLMapRoute simulationRoute;
    private final Map<Integer, OLMapMarker> markers = new HashMap<>();
    private final List<OLMapRoute> routes = new ArrayList<>();

    public OLMap() {
        initConnector();
    }

    public void addMarker(OLMapMarker marker) {
        marker.setMap(this);
        marker.setId(nextLayerId);
        markers.put(marker.getId(), marker);

        getElement().callJsFunction("addMarker", marker.toJson());
        nextLayerId++;
    }

    public void addMarkers(List<OLMapMarker> markers) {
        for(OLMapMarker marker : markers) {
            addMarker(marker);
        }
    }

    public void removeMarker(OLMapMarker marker) {
        if(!markers.containsKey(marker.getId())) {
            return;
        }

        getElement().callJsFunction("removeMarker", marker.toJson());
        marker.setMap(null);
        marker.setId(-1);
        markers.remove(marker.getId());
    }

    public boolean hasMarker(int id) {
        return markers.containsKey(id);
    }

    public OLMapMarker getMarkerFromId(int id) {
        return markers.get(id);
    }

    public List<OLMapMarker> getMarkers() {
        return new ArrayList<>(markers.values());
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

    public void setPositionSimulationRoute(OLMapRoute simulationRoute) {
        this.simulationRoute = simulationRoute;
        getElement().callJsFunction("setPositionSimulationRoute", simulationRoute.toJson());
    }

    public void startPositionSimulation() {
        if(simulationRoute == null) {
            throw new IllegalArgumentException("No simulation route defined!");
        }

        getElement().callJsFunction("startPositionSimulation", 100);
        positionSimulationActive = true;
    }

    public void stopPositionSimulation() {
        getElement().callJsFunction("stopPositionSimulation");
        positionSimulationActive = false;
    }

    public void resetPositionSimulation() {
        if(simulationRoute == null) {
            throw new IllegalArgumentException("No simulation route defined!");
        }

        setPositionSimulationRoute(simulationRoute);
    }

    public boolean isPositionSimulationActive() {
        return positionSimulationActive;
    }

    public void lockPosition() {
        getElement().callJsFunction("lockPosition");
    }

    public void unlockPosition() {
        getElement().callJsFunction("unlockPosition");
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

    private void initConnector() {
        runBeforeClientResponse(ui -> ui.getPage().executeJs("window.Vaadin.Flow.openLayersConnector.initLazy($0)", getElement()));
    }

    private void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui.beforeClientResponse(this, context -> command.accept(ui)));
    }

    @DomEvent("marker_visit")
    public static class MarkerVisitedEvent extends ComponentEvent<OLMap> {
        int markerId = -1;

        public MarkerVisitedEvent(OLMap source,
                                  boolean fromClient,
                                  @EventData("event.detail.markerId") int markerId) {
            super(source, fromClient);
            this.markerId = markerId;
        }

        public int getMarkerId() {
            return markerId;
        }
    }

    @DomEvent("marker_leave")
    public static class MarkerLeaveEvent extends ComponentEvent<OLMap> {
        int markerId = -1;

        public MarkerLeaveEvent(OLMap source,
                                boolean fromClient,
                                @EventData("event.detail.markerId") int markerId) {
            super(source, fromClient);
            this.markerId = markerId;
        }

        public int getMarkerId() {
            return markerId;
        }
    }

    public Registration addMarkerVisitedListener(ComponentEventListener<MarkerVisitedEvent> listener) {
        return addListener(MarkerVisitedEvent.class, listener);
    }

    public Registration addMarkerLeaveListener(ComponentEventListener<MarkerLeaveEvent> listener) {
        return addListener(MarkerLeaveEvent.class, listener);
    }
}
