package com.rki.essenAufRaedern.ui.listener;

import com.rki.essenAufRaedern.backend.service.UserService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    UserService userService;

    @Autowired
    ObjectFactory<HttpSession> httpSessionFactory;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent evt) {
        UserDetails userDetails = (UserDetails) evt.getAuthentication().getPrincipal();
        httpSessionFactory.getObject().setAttribute("userName", userDetails.getUsername());
    }


}
