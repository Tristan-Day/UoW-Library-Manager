package com.libman.display.strategy.search;

import com.libman.display.controller.activity.SearchActivity;
import com.libman.display.controller.activity.inspector.InspectorStrategy;

import javafx.scene.Node;

public abstract class SearchStrategy
{
    protected InspectorStrategy inspector;
    
    public abstract String getTitle();

    public abstract String getPrompt();

    public void setInspector(Node parent, InspectorStrategy inspector) throws Exception
    {
        this.inspector = inspector;

        this.inspector.initalise();
        this.inspector.setParent(parent);
    };

    public abstract void buildResults(String query, SearchActivity search);

    public void inspectResult(Object result) throws Exception
    {
        this.inspector.setTargetObject(result);
        this.inspector.show();
    }
}
