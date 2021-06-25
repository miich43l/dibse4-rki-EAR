package com.rki.essenAufRaedern.ui.views.kitchen;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.entity.Kitchen;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.service.KitchenService;
import com.rki.essenAufRaedern.backend.service.OrderService;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.general.InfoWidgetComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Küche")
@CssImport("./styles/kitchen-view.css")
@Route(value = "kitchen", layout = MainLayout.class)
public class KitchenView extends VerticalLayout {

    private KitchenService kitchenService;
    private OrderService orderService;

    private Kitchen kitchen;

    private Grid<Order> ordersGrid;

    private InfoWidgetComponent numberOfOrdersInfoWidget;
    private InfoWidgetComponent additionalInfosWidget;
    private InfoWidgetComponent driverInfoWidget;

    private Label numberOfOrdersLabel = new Label("0");
    private Label driverLabel = new Label("");
    private Div additionalInfosListContainer = new Div();

    public KitchenView(KitchenService kitchenService, OrderService orderService) {
        this.kitchenService = kitchenService;
        this.orderService = orderService;

        // TODO: kitchen id!
        kitchen = kitchenService.findAll().get(0);

        add(createInfoComponent(), createOrdersComponent());

        updateUI();
    }

    public void updateUI() {
        List<Person> drivers = kitchenService.getDriver(kitchen.getId());

        if(!drivers.isEmpty()) {
            Person driver = drivers.get(0);
            driverLabel.setText(driver.getFullName());
        } else {
            driverLabel.setText("Kein Fahrer");
        }

        ordersGrid.setItems(kitchen.getOrders());
        numberOfOrdersLabel.setText(String.valueOf(kitchen.getOrders().size()));
        additionalInfosListContainer.removeAll();

        kitchen.getOrders().forEach(order -> {
            order.getPerson().getAdditionalInformation(InformationType.KITCHEN).forEach(additionalInformation -> {
                Div content = new Div();
                content.add(new Label(" - " + additionalInformation.getValue()));
                content.setClassName("widget-content-small");
                additionalInfosListContainer.add(content);
            });
        });

        sortOrdersGrid();
    }

    public Component createInfoComponent() {
        HorizontalLayout layout = new HorizontalLayout();

        numberOfOrdersInfoWidget = new InfoWidgetComponent("Bestellungen", numberOfOrdersLabel);
        additionalInfosWidget = new InfoWidgetComponent("Infos", additionalInfosListContainer);
        driverInfoWidget = new InfoWidgetComponent("Fahrer", driverLabel);

        layout.add(numberOfOrdersInfoWidget, /*additionalInfosWidget,*/ driverInfoWidget);
        return layout;
    }

    public Component createOrdersComponent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setClassName("content");

        ordersGrid = new Grid<>(Order.class);

        ordersGrid.removeAllColumns();
        ordersGrid.addColumn(new ComponentRenderer<>(item -> new Div(new Div(new Text(item.getPerson().getFullName())), new Div(new Text(item.getPerson().getAddress().getCity()))))).setHeader("Person").setAutoWidth(true);
        ordersGrid.addColumn("dt").setHeader("Datum").setAutoWidth(true);
        ordersGrid.addColumn(new ComponentRenderer<>(item -> {
            Div div = new Div();
            List<AdditionalInformation> infos = item.getPerson().getAdditionalInformation(InformationType.KITCHEN);
            List<String> infoStrings = infos.stream().map(info -> info.getValue()).collect(Collectors.toList());
            String content = String.join(", ", infoStrings);

            div.add(new Text(content));
            return div;
        })).setHeader("Informationen");


        ordersGrid.addColumn(new ComponentRenderer<>(item -> {
            HorizontalLayout cellLayout = new HorizontalLayout();
            cellLayout.setAlignItems(Alignment.END);
            cellLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            Text stateLabel = new Text(item.getPrepared());
            cellLayout.add(stateLabel);
            Button preparedButton = new Button("Fertig");
            cellLayout.add(preparedButton);
            preparedButton.setEnabled(item.getPrepared() == null);
            preparedButton.addClickListener(buttonClickEvent -> {
                item.setPrepared("Vorbereitet");
                orderService.save(item);
                preparedButton.setEnabled(false);
                stateLabel.setText(item.getPrepared());

                sortOrdersGrid();
            });

            return cellLayout;
        })).setHeader("Status").setAutoWidth(true).setKey("status").setComparator(item -> item.getPrepared() == null).setSortable(false);

        Div content = new Div(ordersGrid);
        content.addClassName("content");
        content.setSizeFull();

        layout.add(content);
        return layout;
    }

    private void sortOrdersGrid() {
        Grid.Column<Order> col = ordersGrid.getColumnByKey("status");
        List<GridSortOrder<Order>> cols = new ArrayList<>();
        cols.add(new GridSortOrder<>(col, SortDirection.ASCENDING));
        ordersGrid.sort(cols);
        cols.clear();
        cols.add(new GridSortOrder<>(col, SortDirection.DESCENDING));
        ordersGrid.sort(cols);
        ordersGrid.getColumnByKey("status").setSortable(false);
    }
}
