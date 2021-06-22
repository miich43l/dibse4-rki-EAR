package com.rki.essenAufRaedern.ui.components.general;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;

@CssImport("./styles/info-widget.css")
public class InfoWidgetComponent extends Div {
    private Label titleLabel = new Label();
    private Div content = new Div();

    public InfoWidgetComponent(String title, Component content) {
        setClassName("widget");

        titleLabel.setText(title);
        this.content.add(content);
        this.content.setClassName("widget-content");

        add(titleLabel, this.content);
    }
}
