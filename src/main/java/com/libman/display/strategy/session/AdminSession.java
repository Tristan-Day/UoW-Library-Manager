package com.libman.display.strategy.session;

// clang-format off
import com.libman.App;
import com.libman.display.controller.activity.SearchActivity;
import com.libman.display.controller.scene.SessionScene;

import com.libman.display.strategy.search.SearchAccounts;
import com.libman.display.strategy.search.SearchPendingAccounts;
import com.libman.display.strategy.search.SearchStrategy;

import javafx.fxml.FXMLLoader;
// clang-format on

public class AdminSession implements SessionStrategy
{

    @Override public void buildSession(SessionScene session) throws Exception
    {
        this.buildPendingUsersActivity(session);
        this.buildSystemUsersActivity(session);
    }

    private void buildSystemUsersActivity(SessionScene session) throws Exception
    {
        // Initalise the search parent activity
        FXMLLoader parentActivity = App.getLoader("activity/SearchActivity");
        session.addActivity("SYSTEM USERS", parentActivity.load());

        // Set search strategy and inspector
        SearchActivity searchController = parentActivity.getController();
        SearchStrategy searchStrategy = new SearchAccounts();

        FXMLLoader inspector = App.getLoader("activity/inspector/AccountInspector");
        session.addActivity(inspector.load());

        searchStrategy.setInspector(searchController.getRoot(), inspector.getController());
        searchController.setStrategy(searchStrategy);
    }

    private void buildPendingUsersActivity(SessionScene session) throws Exception
    {
        // Initalise the search parent activity
        FXMLLoader parentActivity = App.getLoader("activity/SearchActivity");
        session.addActivity("user requests", parentActivity.load());

        // Set search strategy and inspector
        SearchActivity searchController = parentActivity.getController();
        SearchStrategy searchStrategy = new SearchPendingAccounts();

        FXMLLoader inspector = App.getLoader("activity/inspector/PendingAccountInspector");
        session.addActivity(inspector.load());

        searchStrategy.setInspector(searchController.getRoot(), inspector.getController());
        searchController.setStrategy(searchStrategy);
    }
}
