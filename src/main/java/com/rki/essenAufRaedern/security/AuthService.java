package com.rki.essenAufRaedern.security;

import com.rki.essenAufRaedern.backend.entity.User;
import com.rki.essenAufRaedern.backend.service.UserService;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.views.customer.CustomerView;
import com.rki.essenAufRaedern.ui.views.delivery.DeliveryView;
import com.rki.essenAufRaedern.ui.views.kitchen.KitchenView;
import com.rki.essenAufRaedern.ui.views.login.WelcomeView;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class AuthService {

    @Autowired
    HttpSession session;

    @Autowired
    private UserService userService;


    public AuthService() {
    }

    public void createRoutes(MainLayout mainLayout) {
        User user = userService.getUserByUsername((String) session.getAttribute("userName"));
        setAuthorizedRoutes(user.getPersonType(), mainLayout);
    }

    public void setAuthorizedRoutes(PersonType personType, MainLayout mainLayout) {
        if (personType.equals(PersonType.ADMINISTRATION)) {
            mainLayout.addToDrawer(new RouterLink("home", WelcomeView.class));
            mainLayout.addToDrawer(new RouterLink("kunden", CustomerView.class));
            mainLayout.addToDrawer(new RouterLink("kitchen", KitchenView.class));
        } else if (personType.equals(PersonType.KITCHEN)) {
            mainLayout.addToDrawer(new RouterLink("home", WelcomeView.class));
            mainLayout.addToDrawer(new RouterLink("kunden", CustomerView.class));
        } else if (personType.equals(PersonType.DRIVER)) {
            mainLayout.addToDrawer(new RouterLink("home", WelcomeView.class));
            mainLayout.addToDrawer(new RouterLink("kitchen", KitchenView.class));
        } else if (personType.equals(PersonType.CLIENT)) {
            //ToDo create page for client's
            mainLayout.addToDrawer(new RouterLink("home", WelcomeView.class));
        } else if (personType.equals(PersonType.CONTACT_PERSON)) {
            //ToDo create page for contact peron's
            mainLayout.addToDrawer(new RouterLink("home", WelcomeView.class));
        } else if (personType.equals(PersonType.LOCAL_COMMUNITY)) {
            mainLayout.addToDrawer(new RouterLink("home", WelcomeView.class));
            mainLayout.addToDrawer(new RouterLink("kunden", CustomerView.class));
        } else if (personType.equals(PersonType.DEVELOPER)) {
            mainLayout.addToDrawer(new RouterLink("home", WelcomeView.class));
            mainLayout.addToDrawer(new RouterLink("kunden", CustomerView.class));
            mainLayout.addToDrawer(new RouterLink("kitchen", KitchenView.class));
            mainLayout.addToDrawer(new RouterLink("fahrer", DeliveryView.class));
        }
    }

}

