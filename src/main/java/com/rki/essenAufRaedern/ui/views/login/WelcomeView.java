package com.rki.essenAufRaedern.ui.views.login;

import com.rki.essenAufRaedern.ui.MainLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;

import javax.servlet.http.HttpSession;

@Scope("prototype")
@Route(value = "home", layout = MainLayout.class)
@PageTitle("Welcome | Österreichisches Rotes Kreuz")
public class WelcomeView extends VerticalLayout {

    public WelcomeView(ObjectFactory<HttpSession> httpSessionFactory) {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        Image image = new Image("/images/rki.png", "Österreichisches Rotes Kreuz");
        add(image
        );
    }

}