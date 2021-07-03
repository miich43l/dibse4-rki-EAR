package com.rki.essenAufRaedern.ui.views.olmap.tsp;

import com.rki.essenAufRaedern.algorithm.tsp.solver.TspSolverFactory;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.rki.essenAufRaedern.algorithm.tsp.TSP;
import com.rki.essenAufRaedern.algorithm.tsp.api.IRoutingService;
import com.rki.essenAufRaedern.algorithm.tsp.api.RoutingServiceFactory;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPath;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.olmap.OLMap;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapMarker;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapRoute;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "travelingsalesman", layout = MainLayout.class)
@PageTitle("Traveling salesman")
public class TravelingSalesmanView extends VerticalLayout {

    // TODO:
    // - Thomas
    // - refactor

    public TravelingSalesmanView() {
        addClassName("tsp-view");

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();

        TextField addressField = new TextField();
        addressField.setLabel("Address");
        topLayout.add(addressField);

        Button requestCoordinatesButton = new Button("Add");
        topLayout.add(requestCoordinatesButton);

        topLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        add(topLayout);
        add(mainLayout);

        Text coordinatesLabel = new Text("");

        OLMap map = new OLMap();
        map.setHeight(400, Unit.PIXELS);
        map.setWidthFull();
        mainLayout.add(map);

        VerticalLayout markersLayout = new VerticalLayout();
        mainLayout.add(markersLayout);

        Grid<OLMapMarker> markersGrid = new Grid();
        markersGrid.setWidthFull();
        markersGrid.addColumn(OLMapMarker::getTitle).setHeader("Title").setAutoWidth(true);
        markersGrid.addColumn(new ComponentRenderer<Button, OLMapMarker>(item -> {
            Button deleteButton = new Button("remove");
            deleteButton.addClickListener(event -> {
                map.removeMarker(item);
                markersGrid.setItems(map.getMarkers());
            });

            return deleteButton;
        })).setHeader("Action").setAutoWidth(true);

        markersGrid.setItems(map.getMarkers());
        markersLayout.add(markersGrid);

        HorizontalLayout actionLayout = new HorizontalLayout();
        markersLayout.add(actionLayout);

        Button performTspButton = new Button("TSP");
        actionLayout.add(performTspButton);

        Text tspResultLabel = new Text("");
        markersLayout.add(tspResultLabel);

        performTspButton.addClickListener(event -> {
            TSP oTSP = new TSP(RoutingServiceFactory.get().createGraphHopperRoutingService(),
                               TspSolverFactory.get().createBacktrackSolver());
            List<Point2D> pointsToVisit = map.getMarkers().stream().map(OLMapMarker::getCoordinates).collect(Collectors.toList());
            TspPathSequence tspSequence = oTSP.calculateShortestPathSequence(pointsToVisit, 0);
            List<Point2D> tspSequenceCoordinates = tspSequence.getPath().stream().map(pointsToVisit::get).collect(Collectors.toList());

            TspPath tspRoute = oTSP.calculatePathFromCoordinateList(tspSequenceCoordinates);
            List<OLMapMarker> markers = map.getMarkers();
            markers.sort(Comparator.comparingInt(item -> tspSequenceCoordinates.indexOf(item.getCoordinates())));

            StringBuilder strPath = new StringBuilder();
            for(OLMapMarker marker : markers) {
                strPath.append(marker.getTitle());
                strPath.append(" --> ");
            }

            strPath.append(markers.get(0).getTitle());

            System.out.println("Shortest path: " + strPath);
            tspResultLabel.setText(strPath.toString());

            OLMapRoute route = new OLMapRoute(tspRoute.getPoints());
            map.addRoute(route);
        });

        Button clearTspButton = new Button("Clear route");
        actionLayout.add(clearTspButton);

        clearTspButton.addClickListener(event -> {
            map.removeAllRoutes();
        });

        requestCoordinatesButton.addClickListener(event -> {
            IRoutingService service = RoutingServiceFactory.get().createGraphHopperRoutingService();
            Point2D coordinate = service.requestCoordinateFromAddress(addressField.getValue());
            coordinatesLabel.setText(coordinate.toString());

            OLMapMarker newMarker = new OLMapMarker(addressField.getValue(), coordinate);
            map.addMarker(newMarker);
            map.setZoom(10);
            map.setCenter(coordinate);
            markersGrid.setItems(map.getMarkers());
        });
    }

}
