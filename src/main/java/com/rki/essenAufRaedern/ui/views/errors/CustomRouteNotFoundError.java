package com.rki.essenAufRaedern.ui.views.errors;

import com.rki.essenAufRaedern.ui.MainLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.*;

import javax.servlet.http.HttpServletResponse;

@ParentLayout(MainLayout.class)
@PageTitle("page not found")
public class CustomRouteNotFoundError extends RouteNotFoundError {

	public CustomRouteNotFoundError() {
		getElement().appendChild(new Image("/images/NotFoundError.png", "NotFoundError").getElement());
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
		return HttpServletResponse.SC_NOT_FOUND;
	}
}
