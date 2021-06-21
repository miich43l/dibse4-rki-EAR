package com.rki.essenAufRaedern.ui.views.customer;

//import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Person;
        import com.rki.essenAufRaedern.backend.service.AddressService;

        import com.rki.essenAufRaedern.backend.service.PersonService;
        import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.address.AddressEditorComponent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.HashMap;
import java.util.Map;

@PageTitle("Kunden")
@Route(value = "kunden", layout = MainLayout.class)
//@CssImport("./styles/shared-styles.css")

public class CustomerView extends VerticalLayout{

    CustomerForm personForm;
    AddressEditorComponent addressForm;
    Grid<Person> grid = new Grid<>(Person.class);
    TextField filterText = new TextField();
    Dialog editDialog;
    Map<Tab, VerticalLayout> tabViews = new HashMap<>();

    PersonService personService;
    AddressService addressService;

    public CustomerView(PersonService personService, AddressService addressService) {
        this.personService = personService;
        this.addressService = addressService;
        addClassName("customer-view");
        setSizeFull();
        configureGrid();

        Div content = new Div(grid);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void deletePerson() {
        personForm.getPerson().setAddress(null);
        personService.save(personForm.getPerson());

        personService.delete(personForm.getPerson());
        addressService.delete(addressForm.getAddress());

        updateList();
        closeEditor();
    }

    private void validateAndSave() {
        try {
            personForm.validateAndSave();
            addressForm.validateAndSave();
            addressService.save(addressForm.getAddress());
            personService.save(personForm.getPerson());

        } catch (ValidationException e) {
            e.printStackTrace();
            Notification.show("Unable to save data...");
            return;
        }

        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("nach Namen filtern...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        Button addPersonButton = new Button("Kunde hinzufügen", click -> addPerson());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addPersonButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addPerson() {
        grid.asSingleSelect().clear();

        Person newPerson = new Person();
        newPerson.setAddress(new Address());
        newPerson.setStatus(Status.Active);
        newPerson.setPersonType(PersonType.Client);

        editPerson(newPerson);
    }

    private void configureGrid() {
        grid.addClassName("customer-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "address");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(evt -> editPerson(evt.getValue()));
    }

    private void editPerson(Person person) {
        if (person == null) {
            closeEditor();
        } else {
            editDialog = createEditDialog(person);
            editDialog.open();
        }
    }

    private Dialog createEditDialog(Person person) {
        Dialog dialog = new Dialog();
        Tabs tabs = new Tabs();
        dialog.add(tabs);

        // Create person edit form:
        {
            Tab tab = new Tab();
            tab.setLabel("Allgemein");

            VerticalLayout tabLayout = new VerticalLayout();
            personForm = new CustomerForm();
            personForm.setPerson(person);
            tabLayout.add(personForm);

            tabs.add(tab);
            dialog.add(tabLayout);
            tabViews.put(tab, tabLayout);
        }

        // Address:
        {
            Tab tab = new Tab();
            tab.setLabel("Addresse");

            VerticalLayout tabLayout = new VerticalLayout();
            addressForm = new AddressEditorComponent();
            addressForm.setAddress(person.getAddress());
            tabLayout.add(addressForm);
            tabLayout.setVisible(false);

            tabs.add(tab);
            dialog.add(tabLayout);
            tabViews.put(tab, tabLayout);
        }

        // Buttons:
        {
            HorizontalLayout layout = new HorizontalLayout();
            dialog.add(layout);

            Button save = new Button("Speichern");
            Button delete = new Button("Löschen");
            Button close = new Button("Schließen");
            layout.add(save, delete, close);

            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            save.addClickShortcut(Key.ENTER);
            close.addClickShortcut(Key.ESCAPE);

            save.addClickListener(click -> validateAndSave());
            delete.addClickListener(click -> deletePerson());
            close.addClickListener(click -> {
                closeEditor();
            });
        }

        tabs.addSelectedChangeListener(e -> {
            tabViews.keySet().forEach(key -> {
                tabViews.get(key).setVisible(false);
            });

            tabViews.get(e.getSelectedTab()).setVisible(true);
        });

        return dialog;
    }

    private void closeEditor() {
        if(editDialog == null) {
            return;
        }

        editDialog.close();
        editDialog = null;
    }

    private void updateList() {
        grid.setItems(personService.getActiveClients());
    }

}
