package com.rki.essenAufRaedern.ui.views.olmap.geocoding;

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
import com.rki.essenAufRaedern.tsp.api.IRoutingService;
import com.rki.essenAufRaedern.tsp.api.RoutingServiceFactory;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.olmap.OLMap;
import com.rki.essenAufRaedern.ui.components.olmap.OLMapMarker;

import java.awt.geom.Point2D;

@PageTitle("Geocoding")
@Route(value = "geocoding", layout = MainLayout.class)
public class GeocodingView extends VerticalLayout {

    public GeocodingView() {
        addClassName("geocoding-view");

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();

        TextField addressField = new TextField();
        addressField.setLabel("Address");
        topLayout.add(addressField);

        Button requestCoordinatesButton = new Button("Convert to coordinates");
        topLayout.add(requestCoordinatesButton);

        topLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        add(topLayout);
        add(mainLayout);

        Text coordinatesLabel = new Text("");

        OLMap map = new OLMap();
        map.setHeight(400, Unit.PIXELS);
        map.setWidthFull();
        mainLayout.add(map);

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

        mainLayout.add(markersGrid);

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