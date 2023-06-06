package com.libman.display.strategy.session;

// clang-format off
import com.libman.App;
import com.libman.display.controller.activity.SearchActivity;
import com.libman.display.controller.activity.form.PatronRegistrationForm;
import com.libman.display.controller.scene.SessionScene;

import com.libman.display.strategy.search.SearchLoans;
import com.libman.display.strategy.search.SearchPatrons;
import com.libman.display.strategy.search.SearchResources;
import com.libman.display.strategy.search.SearchStrategy;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
// clang-format on

public class LibrarianSession implements SessionStrategy
{

    @Override public void buildSession(SessionScene session) throws Exception
    {
        this.buildPatronRegistrationActivity(session);
        this.buildLoansActivity(session);
        this.buildInventoryActivity(session);
    }

    private void buildPatronRegistrationActivity(SessionScene session) throws IOException
    {
        // Initalise the search parent activity
        FXMLLoader parentActivity = App.getLoader("activity/SearchActivity");
        session.addActivity("PATRONS", parentActivity.load());

        SearchActivity searchController = parentActivity.getController();
        searchController.setStrategy(new SearchPatrons());

        // Initalise the registration form child activity
        FXMLLoader patronRegistrationActivity = App.getLoader("activity/form/PatronRegistrationForm");
        session.addActivity(patronRegistrationActivity.load());

        PatronRegistrationForm patronRegistrationForm = patronRegistrationActivity.getController();
        patronRegistrationForm.setParent(searchController.getRoot());

        // Creating the patron registration button
        Button button = new Button("REGISTER NEW PATRON");

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event)
            {
                patronRegistrationForm.show();
            }
        };

        button.setOnAction(eventHandler);
        searchController.footer.getChildren().add(0, button);
    }

    private void buildLoansActivity(SessionScene session) throws Exception
    {
        // Initalise the search parent activity
        FXMLLoader parentActivity = App.getLoader("activity/SearchActivity");
        session.addActivity("LOANS", parentActivity.load());

        // Set search stategy and inspector
        SearchActivity searchController = parentActivity.getController();
        SearchStrategy searchStrategy = new SearchLoans();

        FXMLLoader inspector = App.getLoader("activity/inspector/LoanInspector");
        session.addActivity(inspector.load());

        searchStrategy.setInspector(searchController.getRoot(), inspector.getController());
        searchController.setStrategy(searchStrategy);
    }

    private void buildInventoryActivity(SessionScene session) throws Exception
    {
        // Initalise the search parent activity
        FXMLLoader parentActivity = App.getLoader("activity/SearchActivity");
        session.addActivity("INVENTORY", parentActivity.load());

        // Set search stategy and inspector
        SearchActivity searchController = parentActivity.getController();
        SearchStrategy searchStrategy = new SearchResources();

        FXMLLoader inspector = App.getLoader("activity/inspector/ResourceInspectorLibrarian");
        session.addActivity(inspector.load());

        searchStrategy.setInspector(searchController.getRoot(), inspector.getController());
        searchController.setStrategy(searchStrategy);
    }
}
