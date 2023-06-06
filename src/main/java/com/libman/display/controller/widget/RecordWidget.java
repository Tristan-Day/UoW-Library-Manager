package com.libman.display.controller.widget;

import com.libman.display.strategy.search.SearchStrategy;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

public class RecordWidget
{
    @FXML private Label title;
    @FXML private Label subtitle;

    @FXML private Region clickableRegion;
    
    @FXML private Label viewLabel;
    @FXML private SVGPath viewIcon;

    private SearchStrategy strategy;
    private Object data;

    public void setStrategy(SearchStrategy strategy)
    {
        this.strategy = strategy;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

    public Object getData()
    {
        return this.data;
    }

    public void allowInspection(Boolean visibility)
    {
        this.clickableRegion.setVisible(visibility);

        this.viewLabel.setVisible(visibility);
        this.viewIcon.setVisible(visibility);
    }

    public void setTitle(String title)
    {
        this.title.setText(title);
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle.setText(subtitle);
    }

    @FXML private void onClick() throws Exception
    {
        this.strategy.inspectResult(this.data);
    };
}
