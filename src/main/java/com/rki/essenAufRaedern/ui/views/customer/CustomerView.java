package com.rki.essenAufRaedern.ui.views.customer;

//import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.service.*;

import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.address.AddressEditorComponent;
import com.rki.essenAufRaedern.ui.components.person.*;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.cookieconsent.CookieConsent;
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
import com.vaadin.flow.component.notification.Notification.Position;

import java.util.HashMap;
import java.util.Map;

@PageTitle("Kunden")
@Route(value = "kunden", layout = MainLayout.class)
//@CssImport("./styles/shared-styles.css")

public class CustomerView extends VerticalLayout{

    CustomerForm personForm;
    AddressEditorComponent addressForm;
    AdditionalInformationComponent additionalInformationForm;
    AdditionalInformationForm addAdditionalInformationForm;
    OrderInformationComponent orderInformationForm;
    ContactPersonComponent contactPersonComponent;
    ContactPersonForm contactPersonForm;
    Grid<Person> grid = new Grid<>(Person.class);
    TextField filterText = new TextField();
    Dialog editDialog;
    Map<Tab, VerticalLayout> tabViews = new HashMap<>();

    PersonService personService;
    AddressService addressService;
    AdditionalInformationService additionalInformationService;
    OrderInformationService orderInformationService;
    ContactPersonService contactPersonService;

    public CustomerView(PersonService personService, AddressService addressService, AdditionalInformationService additionalInformationService, OrderInformationService orderInformationService, ContactPersonService contactPersonService) {
        this.personService = personService;
        this.addressService = addressService;
        this.additionalInformationService = additionalInformationService;
        this.orderInformationService = orderInformationService;
        this.contactPersonService = contactPersonService;
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
            orderInformationService.save(orderInformationForm.getOrderInformation());

        } catch (ValidationException e) {
            e.printStackTrace();
            Notification.show("Achtung! Alle mit * gekennzeichneten Felder in allen Registern müssen befüllt werden.").setPosition(Position.MIDDLE);
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
        OrderInformation orderInformation = new OrderInformation();
        orderInformation.setMonday(Status.Inactive);
        orderInformation.setTuesday(Status.Inactive);
        orderInformation.setWednesday(Status.Inactive);
        orderInformation.setThursday(Status.Inactive);
        orderInformation.setFriday(Status.Inactive);
        orderInformation.setSaturday(Status.Inactive);
        orderInformation.setSunday(Status.Inactive);
        newPerson.addOrderInformation(orderInformation);
        editPerson(newPerson);
    }

    private void configureGrid() {
        grid.addClassName("customer-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "address");
        grid.getColumnByKey("firstName").setHeader("Vorname");
        grid.getColumnByKey("lastName").setHeader("Nachname");
        grid.getColumnByKey("address").setHeader("Adresse");
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
            tab.setLabel("Adresse");

            VerticalLayout tabLayout = new VerticalLayout();
            addressForm = new AddressEditorComponent();
            addressForm.setAddress(person.getAddress());
            tabLayout.add(addressForm);
            tabLayout.setVisible(false);

            tabs.add(tab);
            dialog.add(tabLayout);
            tabViews.put(tab, tabLayout);
        }

        // AdditionalInformation:
        {
            Tab tab = new Tab();
            tab.setLabel("Zusatzinformationen");

            VerticalLayout tabLayout = new VerticalLayout();
            HorizontalLayout addLayout = new HorizontalLayout();
            addLayout.setDefaultVerticalComponentAlignment(Alignment.END);
            addAdditionalInformationForm = new AdditionalInformationForm();
            addAdditionalInformationForm.setAdditionalInformation(new AdditionalInformation());
            Button addInfoButton = new Button("Information hinzufügen", click -> {
                if (!addAdditionalInformationForm.isValid()){
                    return;
                }
                person.addAdditionalInformation(addAdditionalInformationForm.getAdditionalInformation());
                additionalInformationService.save(addAdditionalInformationForm.getAdditionalInformation());
                additionalInformationForm.setPerson(person);
            });
            addLayout.add(addAdditionalInformationForm, addInfoButton);
            tabLayout.add(addLayout);
            additionalInformationForm = new AdditionalInformationComponent();
            additionalInformationForm.setPerson(person);
            additionalInformationForm.setWidthFull();
            additionalInformationForm.addListener(AdditionalInformationComponent.DeleteButtonPressedEvent.class,event ->{
                System.out.println("Event received");
                additionalInformationService.delete(event.getAdditionalInformation());
                additionalInformationForm.setPerson(person);
            });
            tabLayout.add(additionalInformationForm);
            tabLayout.setVisible(false);

            tabs.add(tab);
            dialog.add(tabLayout);
            tabViews.put(tab, tabLayout);
        }

        // OrderInformation - Deliverydays:
        {
            Tab tab = new Tab();
            tab.setLabel("Liefertage");

            VerticalLayout tabLayout = new VerticalLayout();
            orderInformationForm = new OrderInformationComponent();
            orderInformationForm.setOrderInformation(person.getOrderInformation().iterator().next());
            tabLayout.add(orderInformationForm);
            tabLayout.setVisible(false);

            tabs.add(tab);
            dialog.add(tabLayout);
            tabViews.put(tab, tabLayout);
        }

        // ContactPerson:
        /*{
            Tab tab = new Tab();
            tab.setLabel("Kontaktperson");

            VerticalLayout tabLayout = new VerticalLayout();
            HorizontalLayout addLayout = new HorizontalLayout();
            addLayout.setDefaultVerticalComponentAlignment(Alignment.END);
            contactPersonForm = new ContactPersonForm();
            //addAdditionalInformationForm.setAdditionalInformation(new AdditionalInformation());
            addLayout.add(ContactPersonForm);
            tabLayout.add(addLayout);

            tabLayout.add(ContactPersonForm);
            tabLayout.setVisible(false);

            tabs.add(tab);
            dialog.add(tabLayout);
            tabViews.put(tab, tabLayout);
        }
*/
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
