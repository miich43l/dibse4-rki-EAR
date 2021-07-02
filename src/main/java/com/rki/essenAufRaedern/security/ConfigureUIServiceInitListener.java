package com.rki.essenAufRaedern.security;

import com.rki.essenAufRaedern.backend.entity.User;
import com.rki.essenAufRaedern.backend.service.UserService;
import com.rki.essenAufRaedern.ui.views.login.LoginView;
import com.rki.essenAufRaedern.ui.views.login.WelcomeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    @Autowired
    UserService userService;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    private void beforeEnter(BeforeEnterEvent event) {
        User user = userService.getCurrentUser();
        if (null == user) {
            return;
        }
        final boolean accessGranted = SecurityUtils.isAccessGranted(event.getNavigationTarget(), user.getRole());
        if (!accessGranted) {
            if (SecurityUtils.isUserLoggedIn()) {
                event.forwardTo(WelcomeView.class);
            } else {
                event.rerouteTo(LoginView.class);
            }
        }
    }
}
