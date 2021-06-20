package com.rki.essenAufRaedern.ui.views;

import com.rki.essenAufRaedern.backend.entity.Address;
import com.rki.essenAufRaedern.backend.entity.Employee;
import com.rki.essenAufRaedern.backend.entity.Kitchen;
import com.rki.essenAufRaedern.backend.entity.Person;
import com.rki.essenAufRaedern.backend.service.*;
import com.rki.essenAufRaedern.backend.utility.Status;
import com.rki.essenAufRaedern.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Map;

@PageTitle("Test | Vaadin CRM")
@Route(value = "testRepo", layout = MainLayout.class)
public class TestRepo extends VerticalLayout {

    private final ContactService contactService;
    private final CompanyService companyService;
    private final AdditionalInformationService additionalInformationService;
    private final AddressService addressService;
    private final ContactPersonService contactPersonService;
    private final EmployeeService employeeService;
    private final KitchenService kitchenService;
    private final OrderInformationService orderInformationService;
    private final OrderService orderService;
    private final PersonService personService;
    private final UserService userService;

    public TestRepo(ContactService contactService,
                    CompanyService companyService,
                    AdditionalInformationService additionalInformationService,
                    AddressService addressService,
                    ContactPersonService contactPersonService,
                    EmployeeService employeeService,
                    KitchenService kitchenService,
                    OrderInformationService orderInformationService,
                    OrderService orderService,
                    PersonService personService,
                    UserService userService) {
        this.contactService = contactService;
        this.companyService = companyService;
        this.additionalInformationService = additionalInformationService;
        this.addressService = addressService;
        this.contactPersonService = contactPersonService;
        this.employeeService = employeeService;
        this.kitchenService = kitchenService;
        this.orderInformationService = orderInformationService;
        this.orderService = orderService;
        this.personService = personService;
        this.userService = userService;
        

        System.out.println("personService.getActiveClients().stream().findFirst() = " + personService.getActiveClients().stream().findFirst());
        kitchenService.findAll().forEach(k -> employeeService.findByKitchenId(k.getId()).forEach(e -> System.out.println(e.getPerson().getFirstName())));
        //end test

        addClassName("TestRepo");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);


    }

}
