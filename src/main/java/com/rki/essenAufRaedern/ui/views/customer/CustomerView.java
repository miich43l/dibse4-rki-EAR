package com.rki.essenAufRaedern.ui.views.customer;

import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.service.PersonService;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Kunden")
@Route(value = "kunden", layout = MainLayout.class)

public class CustomerView extends VerticalLayout{

    Grid<Person> grid = new Grid<>(Person.class);
    private PersonService personService;

    public CustomerView() {
        this.personService = personService;
        addClassName("customer-view");
        setSizeFull();
        configureGrid();

        add(grid);
        updateList();

    }

    private void updateList() {
        grid.setItems(personService.findAll());
    }

    private void configureGrid() {
        grid.addClassName("customer-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "address");
    }
}



