package com.rki.essenAufRaedern.ui.views.errors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rki.essenAufRaedern.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouteNotFoundError;
import com.vaadin.flow.router.RouterLink;

@ParentLayout(MainLayout.class)
@PageTitle("PAGE NOT FOUND")
public class CustomRouteNotFoundError extends RouteNotFoundError {

	public CustomRouteNotFoundError() {
		RouterLink link = Component.from(
				ElementFactory.createRouterLink("home", "Go to the front page."),
				RouterLink.class);
		getElement().appendChild(new Text("so leicht geht's nicht ;) ").getElement(), link.getElement());
		getElement().appendChild(new Image("/images/NotFoundError.png", "").getElement(), link.getElement());
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
		return HttpServletResponse.SC_NOT_FOUND;
	}
}
