package com.libman.display.controller.scene;

// clang-format off
import com.libman.App;
import com.libman.display.WidgetDecorator;
import com.libman.display.controller.StageController;
import com.libman.display.strategy.session.AdminSession;
import com.libman.display.strategy.session.CuratorSession;
import com.libman.display.strategy.session.LibrarianSession;
import com.libman.display.strategy.session.SessionStrategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.util.Pair;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
// clang-format on

public class SessionScene extends StageController
{
    @FXML private ToggleButton home;

    @FXML private Label greeting;
    @FXML private Label role;

    @FXML private VBox navbar;
    @FXML private StackPane stack;

    private Map<ToggleButton, Node> activities;
    private Pair<ToggleButton, Node> currentActivity;

    private SessionStrategy strategy;

    @Override protected void show()
    {
        // Set common sesssion attributes
        this.greeting.setText("Welcome, " + com.libman.data.Session.get().account.firstname);
        this.role.setText(com.libman.data.Session.get().account.type.toString().toUpperCase());

        // Create and set the 'home' activity
        this.activities = new HashMap<>() {
            {
                put(home, (GridPane)stack.getChildren().get(0));
            }
        };
        this.currentActivity = new Pair<>(home, this.activities.get(home));
        this.currentActivity.getKey().setSelected(true);

        // Select session strategy from account type
        switch (com.libman.data.Session.get().account.type.name)
        {
        case "Librarian":
            this.strategy = new LibrarianSession();
            break;

        case "Curator":
            this.strategy = new CuratorSession();
            break;

        case "Administrator":
            this.strategy = new AdminSession();
            break;

        default:
            System.out.println("No Known Session Strategy");
            break;
        }

        // Attempt to build the session
        try
        {
            this.strategy.buildSession(this);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void addActivity(String name, GridPane activity)
    {
        ToggleButton button = new ToggleButton(name.toUpperCase());
        button.setOnAction(this::handleSwitchActivity);
        button.setMinWidth(190);

        this.navbar.getChildren().add(1, button);
        this.stack.getChildren().add(0, activity);

        this.activities.put(button, activity);
    }

    public void addActivity(Node activity)
    {
        this.stack.getChildren().add(0, activity);
    }

    @FXML private void handleSwitchActivity(ActionEvent event)
    {
        this.currentActivity.getKey().setSelected(false);

        ToggleButton activity = (ToggleButton)event.getSource();

        this.activities.get(activity).toFront();
        this.currentActivity = new Pair<ToggleButton, Node>(activity, this.activities.get(activity));

        this.currentActivity.getKey().setSelected(true);
    }

    @FXML private void handleLogout() throws IOException
    {
        Alert dialog = WidgetDecorator.style(new Alert(Alert.AlertType.CONFIRMATION), "Session");
        
        dialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        dialog.setHeaderText("Are you sure you want to Logout?");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES)
        {
            App.setRoot(App.getLoader("AuthenticationScene").load());
        }
    }
}
