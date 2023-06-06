package com.libman.display.controller.scene;

// clang-format off
import com.libman.App;
import com.libman.display.WidgetDecorator;
import com.libman.display.controller.StageController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
// clang-format on

public class DatabaseSetupScene extends StageController
{
    @FXML private Label note;

    @FXML private TextField host;
    @FXML private TextField port;

    @FXML private TextField username;
    @FXML private TextField password;

    Map<String, String> configuration;

    public void setNote(String note)
    {
        this.note.setText(note);
    }

    @Override protected void show()
    {
        configuration = (Map<String, String>)App.preferences.get("database");

        this.host.setText(configuration.get("host"));
        this.port.setText(configuration.get("port"));

        this.username.setText(configuration.get("username"));
        this.password.setText(configuration.get("password"));
    }

    @FXML private void handleApply() throws IOException
    {
        // Validate port number
        if (this.port.getText().matches("[A-Za-z]+"))
        {
            Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Database Setup");
            alert.setContentText("Port must only contain numeric characters.");

            alert.show();
            return;
        }

        // Write new configuration to preferences
        configuration.put("host", this.host.getText());
        configuration.put("port", this.port.getText());

        configuration.put("username", this.username.getText());
        configuration.put("password", this.password.getText());

        App.preferences.put("database", configuration);
        new Yaml().dump(App.preferences, new FileWriter("./src/main/resources/com/libman/preferences.yml"));

        // Attempt to connect using the new configuration
        try
        {
            App.database.initialize();
            App.setRoot(App.getLoader("AuthenticationScene").load());
        }
        catch (SQLException exception)
        {
            Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Database Setup");
            alert.setHeaderText("Failed to Connect to Database");
            alert.setContentText(exception.getMessage());
            alert.show();
        }
    }
}
