package com.rki.essenAufRaedern.ui;

import com.rki.essenAufRaedern.backend.service.UserService;
import com.rki.essenAufRaedern.security.SecurityUtils;
import com.rki.essenAufRaedern.ui.views.customer.CustomerView;
import com.rki.essenAufRaedern.ui.views.delivery.DeliveryView;
import com.rki.essenAufRaedern.ui.views.kitchen.KitchenView;
import com.rki.essenAufRaedern.ui.views.login.WelcomeView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;


@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    private final ConfirmDialog confirmDialog = new ConfirmDialog();

    @Autowired
    private UserService userService;

    public MainLayout(UserService userService) {
        this.userService = userService;
        createHeader();
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmButtonTheme("raised tertiary error");
        confirmDialog.setCancelButtonTheme("raised tertiary");
        createDrawer();
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

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        confirmDialog.setOpened(false);
        if (!RouteConfiguration.forSessionScope().isRouteRegistered(this.getContent().getClass())){
            //toDo add error page
        }
    }

    private void createDrawer() {
        String role = userService.getUserByUsernameIgnoreCase(SecurityUtils.getUsername()).getRole();
        addToDrawer(new VerticalLayout(new RouterLink("home", WelcomeView.class)));

        if (SecurityUtils.isAccessGranted(KitchenView.class, role)) {
            addToDrawer(new VerticalLayout(new RouterLink("Küche", KitchenView.class)));
        }
        if (SecurityUtils.isAccessGranted(DeliveryView.class, role)) {
            addToDrawer(new VerticalLayout(new RouterLink("Fahrer", DeliveryView.class)));
        }
        if (SecurityUtils.isAccessGranted(CustomerView.class, role)) {
            addToDrawer(new VerticalLayout(new RouterLink("Kunden", CustomerView.class)));
        }

    }
}
