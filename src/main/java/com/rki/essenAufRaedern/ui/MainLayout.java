package com.rki.essenAufRaedern.ui;

import com.rki.essenAufRaedern.ui.views.customer.CustomerView;
import com.rki.essenAufRaedern.ui.views.delivery.DeliveryView;
import com.rki.essenAufRaedern.ui.views.kitchen.KitchenView;
import com.rki.essenAufRaedern.ui.views.login.WelcomeView;
import com.rki.essenAufRaedern.ui.views.olmap.geocoding.GeocodingView;
import com.rki.essenAufRaedern.ui.views.olmap.tsp.TravelingSalesmanView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;

@PWA(
        name = "Österreichisches Rotes Kreuz",
        shortName = "CRM",
        offlineResources = {
                "./styles/offline.css",
                "./images/offline.png"
        },
        enableInstallPrompt = false
)
@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
        /*AuthService authService = new AuthService();
        authService.createRoutes(this);
         */

    }

    private void createHeader() {
        H1 logo = new H1("Österreichisches Rotes Kreuz");
        logo.addClassName("logo");

        Anchor logout = new Anchor("/logout", "Log out");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);
        header.addClassName("header");
        header.setWidth("100%");
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer() {
        addToDrawer(new VerticalLayout(
                new RouterLink("home", WelcomeView.class),
                new RouterLink("Kunden", CustomerView.class),
                new RouterLink("Küche", KitchenView.class),
                new RouterLink("Fahrer", DeliveryView.class),
                new RouterLink("Geocoding", GeocodingView.class),
                new RouterLink("Traveling salesman", TravelingSalesmanView.class)

        ));
    }

}
