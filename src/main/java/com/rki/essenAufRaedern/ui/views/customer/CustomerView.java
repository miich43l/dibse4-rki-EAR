package com.rki.essenAufRaedern.ui.views.customer;

import com.rki.essenAufRaedern.backend.entity.*;
import com.rki.essenAufRaedern.backend.service.*;
import com.rki.essenAufRaedern.backend.utility.PersonType;
import com.rki.essenAufRaedern.backend.utility.Status;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.rki.essenAufRaedern.ui.components.person.*;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * View for administrators to create and manage customers
 * - it shows all customers in a grid
 * - it has a filter function to filter by name or last name
 * - customers can be created and edited (address, additional information, delivery days, contact person)
 */

@PageTitle("Kunden")
@Route(value = "kunden", layout = MainLayout.class)
@Secured({"ADMINISTRATION", "LOCAL_COMMUNITY", "DEVELOPER"})
public class CustomerView extends VerticalLayout {

    // UI Elements:
    private GeneralCustomerForm personForm;
    private AddressEditorComponent addressForm;
    private AdditionalInformationComponent additionalInformationComponent;
    private AdditionalInformationForm addAdditionalInformationForm;
    private OrderInformationComponent orderInformationComponent;
    private ContactPersonComponent contactPersonComponent;
    private ContactPersonForm contactPersonForm;
    private Dialog editDialog;
    private final Grid<Person> customerGrid = new Grid<>(Person.class);
    private final TextField filterText = new TextField();
    private final Map<Tab, VerticalLayout> tabViews = new HashMap<>();

    // Services:
    private final PersonService personService;
    private final AddressService addressService;
    private final AdditionalInformationService additionalInformationService;
    private final OrderInformationService orderInformationService;
    private final ContactPersonService contactPersonService;

    public CustomerView(PersonService personService, AddressService addressService, AdditionalInformationService additionalInformationService, OrderInformationService orderInformationService, ContactPersonService contactPersonService) {
        this.personService = personService;
        this.addressService = addressService;
        this.additionalInformationService = additionalInformationService;
        this.orderInformationService = orderInformationService;
        this.contactPersonService = contactPersonService;

        addClassName("customer-view");
        setSizeFull();
        configureGrid();

        Div content = new Div(customerGrid);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
        updateSearchList();
    }

    private void addPerson() {
        customerGrid.asSingleSelect().clear();
        Person newPerson = createNewPerson(PersonType.CLIENT);
        editPerson(newPerson);
    }

    private void editPerson(Person person) {
        if (person == null) {
            closeEditor();
        } else {
            editDialog = createEditDialog(person);
            editDialog.open();
        }
    }

    private void validateAndSave(Person person) {
        try {
            personForm.validateAndSave();
            addressForm.validateAndSave();
            addressService.save(person.getAddress());
            personService.save(person);
            personService.saveAllUnsavedAdditionalInformation(person);
            personService.saveAllUnsavedContactPersons(person);
            orderInformationService.save(orderInformationComponent.getOrderInformation());

        } catch (ValidationException e) {
            e.printStackTrace();
            Notification.show("Achtung! Alle mit * gekennzeichneten Felder in allen Registern m??ssen bef??llt werden.").setPosition(Position.MIDDLE);
            return;
        }

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

    private Dialog createEditDialog(Person person) {
        Dialog dialog = new Dialog();
        Tabs tabs = new Tabs();
        dialog.add(tabs);

        createPersonDialogTab(person, dialog, tabs);
        createAddressDialogTab(person, dialog, tabs);
        createAdditionalInformationDialogTab(person, dialog, tabs);
        createOrderInformationDialogTab(person, dialog, tabs);
        createContactPersonDialogTab(person, dialog, tabs);
        createDialogButtonsLayout(person, dialog);

        tabs.addSelectedChangeListener(e -> {
            tabViews.keySet().forEach(key -> {
                tabViews.get(key).setVisible(false);
            });

            tabViews.get(e.getSelectedTab()).setVisible(true);
        });

        return dialog;
    }

    private void createPersonDialogTab(Person person, Dialog dialog, Tabs tabs) {
        Tab tab = new Tab();
        tab.setLabel("Allgemein");

        VerticalLayout tabLayout = new VerticalLayout();
        personForm = new GeneralCustomerForm();
        personForm.setPerson(person);
        tabLayout.add(personForm);

        tabs.add(tab);
        dialog.add(tabLayout);
        tabViews.put(tab, tabLayout);
    }

    private void createAddressDialogTab(Person person, Dialog dialog, Tabs tabs) {
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

    private void createAdditionalInformationDialogTab(Person person, Dialog dialog, Tabs tabs) {
        Tab tab = new Tab();
        tab.setLabel("Zusatzinformationen");

        VerticalLayout tabLayout = new VerticalLayout();
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        addAdditionalInformationForm = new AdditionalInformationForm();
        addAdditionalInformationForm.setAdditionalInformation(new AdditionalInformation());
        Button addInfoButton = new Button("Information hinzuf??gen", click -> {
            if (!addAdditionalInformationForm.isValid()) {
                return;
            }
            System.out.println("Event received ID: " + addAdditionalInformationForm.getAdditionalInformation().getId());

            person.addAdditionalInformation(addAdditionalInformationForm.getAdditionalInformation());
            additionalInformationComponent.setPerson(person);
            addAdditionalInformationForm.setAdditionalInformation(new AdditionalInformation());
        });
        addLayout.add(addAdditionalInformationForm, addInfoButton);
        tabLayout.add(addLayout);
        additionalInformationComponent = new AdditionalInformationComponent();
        additionalInformationComponent.setPerson(person);
        additionalInformationComponent.setWidthFull();
        additionalInformationComponent.addListener(AdditionalInformationComponent.DeleteButtonPressedEvent.class, event -> {
            System.out.println("Event received ID: " + event.getAdditionalInformation().getId());
            if (event.getAdditionalInformation().getId() == null) {
                person.removeAdditionalInformation(event.getAdditionalInformation());
            } else {
                additionalInformationService.delete(event.getAdditionalInformation());
            }

            additionalInformationComponent.setPerson(person);
        });
        tabLayout.add(additionalInformationComponent);
        tabLayout.setVisible(false);

        tabs.add(tab);
        dialog.add(tabLayout);
        tabViews.put(tab, tabLayout);
    }

    private void createOrderInformationDialogTab(Person person, Dialog dialog, Tabs tabs) {
        Tab tab = new Tab();
        tab.setLabel("Liefertage");

        VerticalLayout tabLayout = new VerticalLayout();
        orderInformationComponent = new OrderInformationComponent();
        orderInformationComponent.setOrderInformation(person.getOrderInformation().iterator().next());
        tabLayout.add(orderInformationComponent);
        tabLayout.setVisible(false);

        tabs.add(tab);
        dialog.add(tabLayout);
        tabViews.put(tab, tabLayout);
    }


    private void createContactPersonDialogTab(Person person, Dialog dialog, Tabs tabs) {
        Tab tab = new Tab();
        tab.setLabel("Kontaktperson");

        VerticalLayout tabLayout = new VerticalLayout();
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        contactPersonForm = new ContactPersonForm();
        contactPersonForm.setContactPerson(createNewContactPerson());
        addLayout.add(contactPersonForm);
        tabLayout.add(addLayout);

        Button addContactPersonButton = new Button("Hinzuf??gen", e -> {
            if (!contactPersonForm.isValid()) {
                Notification.show("Kontaktperson ung??ltig.");
                return;
            }

            ContactPerson newContactPerson = contactPersonForm.getContactPerson();
            newContactPerson.setPerson(person);
            person.addContactPerson(newContactPerson);

            contactPersonComponent.setPerson(person);
            contactPersonForm.setContactPerson(createNewContactPerson());
        });

        tabLayout.add(addContactPersonButton);

        contactPersonComponent = new ContactPersonComponent(new ContactPersonComponent.Config().allowDelete(true).allowCall(false));
        contactPersonComponent.setPerson(person);
        contactPersonComponent.addListener(ContactPersonComponent.DeleteButtonPressedEvent.class, e -> {
            ContactPerson contactPerson = e.getContactPerson();
            personService.delete(contactPerson.getPerson());

            System.out.println("BEFORE Contact persons: " + person.getContactPersons() + " - " + person.getContactPersonFrom());

            contactPerson.setPerson(null);
            person.removeContactPerson(contactPerson);

            System.out.println("AFTER Contact persons: " + person.getContactPersons() + " - " + person.getContactPersonFrom());

            contactPersonService.delete(contactPerson);
            contactPersonComponent.setPerson(person);
            personService.save(person);
        });

        tabLayout.add(contactPersonComponent);
        tabLayout.setVisible(false);

        tabs.add(tab);
        dialog.add(tabLayout);
        tabViews.put(tab, tabLayout);
    }

    private void createDialogButtonsLayout(Person person, Dialog dialog) {
        HorizontalLayout layout = new HorizontalLayout();
        dialog.add(layout);

        Button save = new Button("Speichern");
        Button delete = new Button("L??schen");
        Button close = new Button("Schlie??en");
        layout.add(save, delete, close);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave(person));
        delete.addClickListener(click -> deletePerson());
        close.addClickListener(click -> {
            closeEditor();
        });
    }


    public Person createNewPerson(PersonType personType) {
        Person newPerson = new Person();
        newPerson.setStatus(Status.ACTIVE);
        newPerson.setPersonType(personType);

        if (personType == PersonType.CLIENT) {
            Address address = new Address();
            address.setStatus(Status.ACTIVE);
            newPerson.setAddress(address);

            OrderInformation orderInformation = new OrderInformation();
            orderInformation.setStatus(Status.ACTIVE);
            orderInformation.setMonday(Status.INACTIVE);
            orderInformation.setTuesday(Status.INACTIVE);
            orderInformation.setWednesday(Status.INACTIVE);
            orderInformation.setThursday(Status.INACTIVE);
            orderInformation.setFriday(Status.INACTIVE);
            orderInformation.setSaturday(Status.INACTIVE);
            orderInformation.setSunday(Status.INACTIVE);
            orderInformation.setDt_form(new Date());

            newPerson.addOrderInformation(orderInformation);
        }

        return newPerson;
    }


    private ContactPerson createNewContactPerson() {
        ContactPerson contactPerson = new ContactPerson();
        Person person = createNewPerson(PersonType.CONTACT_PERSON);
        person.addContactPersonFrom(contactPerson);

        return contactPerson;
    }


    private void closeEditor() {
        if (editDialog == null) {
            return;
        }

        editDialog.close();
        editDialog = null;
    }

    private void updateList() {
        customerGrid.setItems(personService.getActiveClients());
    }

    private void updateSearchList() {
        customerGrid.setItems(personService.getActiveClientsBySearchFieldInput(filterText.getValue()));
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("nach Namen filtern...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateSearchList());

        Button addPersonButton = new Button("Kunde hinzuf??gen", click -> addPerson());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addPersonButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        customerGrid.addClassName("customer-grid");
        customerGrid.setSizeFull();
        customerGrid.setColumns("firstName", "lastName", "address");
        customerGrid.getColumnByKey("firstName").setHeader("Vorname");
        customerGrid.getColumnByKey("lastName").setHeader("Nachname");
        customerGrid.getColumnByKey("address").setHeader("Adresse");
        customerGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        customerGrid.asSingleSelect().addValueChangeListener(evt -> editPerson(evt.getValue()));
    }
}
