package com.rki.essenAufRaedern.security;

import com.rki.essenAufRaedern.backend.service.UserService;
import com.vaadin.flow.server.HandlerHelper.RequestType;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class SecurityUtils {

    private static UserService userService;

    private SecurityUtils(UserService userService) {
        this.userService = userService;
    }

    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    public static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
    }

    public static boolean isAccessGranted(Class<?> securedClass, String role) {
        Secured secured = AnnotationUtils.findAnnotation(securedClass, Secured.class);
        if (secured == null) {
            return true;
        }
        List<String> allowedRoles = Arrays.asList(secured.value());
        return allowedRoles.stream().anyMatch(aRole -> aRole.equals(role));
    }

    public static String getUsername() {
        return String.valueOf(getPrincipal().getUsername());
    }

    public static boolean hasRole(String role) {
        return getPrincipal().getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    public static UserDetails getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return null;
        }

        return (UserDetails) authentication.getPrincipal();
    }
}
