package com.rki.essenAufRaedern.ui.views.customer;

import com.rki.essenAufRaedern.backend.entity.Company;

//import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.service.AddressService;
import com.rki.essenAufRaedern.backend.service.CompanyService;

import com.rki.essenAufRaedern.backend.service.PersonService;
import com.rki.essenAufRaedern.backend.service.PersonService;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.views.customer.CustomerForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Kunden")
@Route(value = "kunden", layout = MainLayout.class)
//@CssImport("./styles/shared-styles.css")

public class CustomerView extends VerticalLayout{

    CustomerForm form;
    Grid<Person> grid = new Grid<>(Person.class);
    TextField filterText = new TextField();

    PersonService personService;
    AddressService addressService;

    public CustomerView(PersonService personService, AddressService addressService) {
        this.personService = personService;
        this.addressService = addressService;
        addClassName("customer-view");
        setSizeFull();
        configureGrid();


        form = new CustomerForm();
        form.addListener(CustomerForm.SaveEvent.class, this::savePerson);
        form.addListener(CustomerForm.DeleteEvent.class, this::deletePerson);
        form.addListener(CustomerForm.CloseEvent.class, e -> closeEditor());

        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void deletePerson(CustomerForm.DeleteEvent evt) {
        personService.delete(evt.getPerson());
        updateList();
        closeEditor();
    }

    private void savePerson(CustomerForm.SaveEvent evt) {
        evt.getPerson().setPersonType(PersonType.Client);
        evt.getPerson().setStatus(Status.Active);

        System.out.println("Address: " + evt.getAddress());

        addressService.save(evt.getAddress());
        evt.getPerson().setAddress(evt.getAddress());
        personService.save(evt.getPerson());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("nach Namen filtern...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        //filterText.addValueChangeListener(e -> updateList());

        Button addPersonButton = new Button("Kunde hinzufügen", click -> addPerson());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addPersonButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addPerson() {
        grid.asSingleSelect().clear();
        editPerson(new Person());
    }

    private void configureGrid() {
        grid.addClassName("customer-grid");
        grid.setSizeFull();
        //grid.removeColumnByKey("company");
        grid.setColumns("firstName", "lastName", "address");
       /* grid.addColumn(person -> {
            Company company = person.getCompany();
            return company == null ? "-" : company.getName();
        }).setHeader("Company");*/

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> editPerson(evt.getValue()));
    }

    private void editPerson(Person person) {
        if (person == null) {
            closeEditor();
        } else {
            form.setPerson(person);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setPerson(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(personService.findAll());
    }

}
