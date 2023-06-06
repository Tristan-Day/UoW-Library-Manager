package com.libman.display.controller.activity;

import javafx.fxml.FXML;
import javafx.scene.Node;

public abstract class Activity
{
    @FXML protected Node root;

    protected Node parent;

    public void show()
    {
        this.root.toFront();
    }

    public void hide()
    {
        this.root.toBack();
    }

    public Node getParent()
    {
        return this.parent;
    }

    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    @FXML protected void handleReturn()
    {
        this.parent.toFront();
    }
}
