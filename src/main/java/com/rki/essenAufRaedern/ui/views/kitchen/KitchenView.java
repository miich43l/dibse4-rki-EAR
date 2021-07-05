package com.rki.essenAufRaedern.ui.views.kitchen;

import com.rki.essenAufRaedern.backend.entity.AdditionalInformation;
import com.rki.essenAufRaedern.backend.entity.Kitchen;
import com.rki.essenAufRaedern.backend.entity.Order;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.service.AdditionalInformationService;
import com.rki.essenAufRaedern.backend.service.KitchenService;
import com.rki.essenAufRaedern.backend.service.OrderService;
import com.rki.essenAufRaedern.backend.utility.InformationType;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.general.InfoWidgetComponent;
import com.rki.essenAufRaedern.util.Util;
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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Thomas Widmann
 * View for the kitchen.
 * It shows the orders for a given date
 * and some general informations like the driver.
 */

@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Küche")
@CssImport("./styles/kitchen-view.css")
@Route(value = "kitchen", layout = MainLayout.class)
@Secured({"ADMINISTRATION", "KITCHEN", "DEVELOPER"})
public class KitchenView extends VerticalLayout {

    private final KitchenService kitchenService;
    private final OrderService orderService;
    private final AdditionalInformationService additionalInformationService;
    private final Kitchen kitchen;
    private Date ordersDate = new Date();

    private final Label numberOfOrdersLabel = new Label("0");
    private final Label numberOfOrdersDoneLabel = new Label("0");
    private final Label driverLabel = new Label("");
    private Grid<Order> ordersGrid;

    public KitchenView(KitchenService kitchenService, OrderService orderService, AdditionalInformationService additionalInformationService) {
        this.kitchenService = kitchenService;
        this.orderService = orderService;
        this.additionalInformationService = additionalInformationService;

        setClassName("main-layout");

        kitchen = kitchenService.getKitchenForLoggedInEmployee();

        add(createDaySelectionTab(), createInfoComponent(), createOrdersComponent());

        updateUI();
    }

    public void updateUI() {
        if(this.kitchen == null) {
            return;
        }

        List<Person> drivers = kitchenService.getDriver(kitchen.getId());

        if (!drivers.isEmpty()) {
            Person driver = drivers.get(0);
            driverLabel.setText(driver.getFullName());
        } else {
            driverLabel.setText("Kein Fahrer");
        }

        List<Order> orders = orderService.getOrdersForKitchenAndDay(kitchen.getId(), ordersDate);
        ordersGrid.setItems(orders);
        //ordersGrid.getColumnByKey("status").setVisible(Util.DateUtil.isToday(date));

        long numberOfCompletedOrders = orders.stream().filter(order -> order.getPrepared() != null).count();

        numberOfOrdersLabel.setText("Gesamt: " + orders.size());
        numberOfOrdersDoneLabel.setText("Fertig: " + numberOfCompletedOrders);

        sortOrdersGrid();
    }

    public Component createDaySelectionTab() {
        Tabs dayTabs = new Tabs();
        dayTabs.setWidthFull();
        Date now = new Date();

        for (int n = 0; n < 7; n++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, n);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            String dayOfWeekName = Util.DateUtil.getDayOfWeekName(dayOfWeek);

            Tab dayTab = new Tab();
            dayTab.setLabel(dayOfWeekName);

            dayTabs.add(dayTab);
        }

        dayTabs.addSelectedChangeListener(selectedChangeEvent -> {
            ordersDate = Util.DateUtil.getDayFromNow(dayTabs.getSelectedIndex());
            updateUI();
        });

        return dayTabs;
    }

    public Component createInfoComponent() {
        HorizontalLayout layout = new HorizontalLayout();

        Div ordersLayout = new Div();
        ordersLayout.add(new Div(numberOfOrdersDoneLabel));
        ordersLayout.add(new Div(numberOfOrdersLabel));
        InfoWidgetComponent numberOfOrdersInfoWidget = new InfoWidgetComponent("Bestellungen", ordersLayout);
        InfoWidgetComponent driverInfoWidget = new InfoWidgetComponent("Fahrer", driverLabel);

        layout.add(numberOfOrdersInfoWidget, driverInfoWidget);
        return layout;
    }

    public Component createOrdersComponent() {
        ordersGrid = new Grid<>(Order.class);
        ordersGrid.setHeightFull();

        ordersGrid.removeAllColumns();
        ordersGrid.addColumn(new ComponentRenderer<>(item -> new Div(new Div(new Text(item.getPerson().getFullName())), new Div(new Text(item.getPerson().getAddress().getCity()))))).setHeader("Person").setAutoWidth(true);
        ordersGrid.addColumn("dt").setHeader("Datum").setAutoWidth(true);
        ordersGrid.addColumn(new ComponentRenderer<>(order -> {
            Div div = new Div();
            List<AdditionalInformation> additionalInformations = additionalInformationService.findForPersonAndType(order.getPerson().getId(), InformationType.KITCHEN);
            List<String> infoStrings = additionalInformations.stream().map(AdditionalInformation::getValue).collect(Collectors.toList());
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

                updateUI();
                sortOrdersGrid();
            });

            return cellLayout;
        })).setHeader("Status").setAutoWidth(true).setKey("status").setComparator(item -> item.getPrepared() == null).setSortable(false);

        return ordersGrid;
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
