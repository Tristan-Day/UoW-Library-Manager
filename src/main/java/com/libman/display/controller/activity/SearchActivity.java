package com.libman.display.controller.activity;

import com.libman.display.strategy.search.SearchStrategy;

// clang-format off
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
// clang-format on

public class SearchActivity
{
    @FXML private GridPane root;

    @FXML private Label title;
    @FXML private TextField query;

    @FXML private VBox results;
    @FXML public HBox footer;

    private SearchStrategy strategy;

    public void setStrategy(SearchStrategy strategy)
    {
        this.strategy = strategy;

        this.title.setText(this.strategy.getTitle());
        this.query.setPromptText(this.strategy.getPrompt());
    }

    public GridPane getRoot()
    {
        return this.root;
    }

    public void addResult(Node result)
    {
        this.results.getChildren().add(0, result);
    }

    @FXML private void handleSearch()
    {
        this.results.getChildren().clear();
        this.strategy.buildResults(this.query.getText(), this);
    }

    @FXML private void handleClearSearch()
    {
        this.query.setText("");
        this.handleSearch();
    }
}
