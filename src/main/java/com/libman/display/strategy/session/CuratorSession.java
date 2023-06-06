package com.libman.display.strategy.session;

// clang-format off
import com.libman.App;
import com.libman.display.controller.activity.SearchActivity;
import com.libman.display.controller.activity.form.ResourceEditorForm;
import com.libman.display.controller.scene.SessionScene;

import com.libman.display.strategy.search.SearchResources;
import com.libman.display.strategy.search.SearchStrategy;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
// clang-format on

public class CuratorSession implements SessionStrategy
{

    @Override public void buildSession(SessionScene session) throws Exception
    {
        // Initalise the search parent activity
        FXMLLoader parentActivity = App.getLoader("activity/SearchActivity");
        session.addActivity("INVENTORY", parentActivity.load());

        // Set search strategy and inspector
        SearchActivity searchController = parentActivity.getController();
        SearchStrategy searchStrategy = new SearchResources();

        FXMLLoader inspector = App.getLoader("activity/inspector/ResourceInspectorCurator");
        session.addActivity(inspector.load());

        searchStrategy.setInspector(searchController.getRoot(), inspector.getController());
        searchController.setStrategy(searchStrategy);

        // Initalise the resource registration form child activity
        FXMLLoader resourceRegistrationActivity = App.getLoader("activity/form/ResourceEditorForm");
        session.addActivity(resourceRegistrationActivity.load());

        ResourceEditorForm resourceRegistrationForm = resourceRegistrationActivity.getController();
        resourceRegistrationForm.setParent(searchController.getRoot());
        resourceRegistrationForm.initalise();

        // Creating the resource registration button
        Button button = new Button("REGISTER NEW RESOURCE");

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event)
            {
                resourceRegistrationForm.show();
                try
                {
                    resourceRegistrationForm.setTargetObject(null);
                }
                catch (Exception exception)
                {
                }
            }
        };

        button.setOnAction(eventHandler);
        searchController.footer.getChildren().add(0, button);
    }
}
