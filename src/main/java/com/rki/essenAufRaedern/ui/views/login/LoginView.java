package com.rki.essenAufRaedern.ui.views.login;

import com.rki.essenAufRaedern.security.SecurityUtils;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

@Route("login")
    @PageTitle("Login | Vaadin CRM")
    public class LoginView extends LoginOverlay
            implements AfterNavigationObserver, BeforeEnterObserver {

        public LoginView() {
            LoginI18n i18n = LoginI18n.createDefault();
            i18n.setHeader(new LoginI18n.Header());
            i18n.getHeader().setTitle("Österreichisches Rotes Kreuz");
            //i18n.getHeader().setDescription();
            i18n.setAdditionalInformation(null);
            i18n.setForm(new LoginI18n.Form());
            i18n.getForm().setSubmit("Sign in");
            i18n.getForm().setTitle("Sign in");
            i18n.getForm().setUsername("Username");
            i18n.getForm().setPassword("Password");
            setI18n(i18n);
            setForgotPasswordButtonVisible(false);
            setAction("login");
        }

        @Override
        public void beforeEnter(BeforeEnterEvent event) {
            if (SecurityUtils.isUserLoggedIn()) {
                event.forwardTo(WelcomeView.class);
            } else {
                setOpened(true);
            }
        }

        @Override
        public void afterNavigation(AfterNavigationEvent event) {
            setError(
                    event.getLocation().getQueryParameters().getParameters().containsKey(
                            "error"));
        }

    }

