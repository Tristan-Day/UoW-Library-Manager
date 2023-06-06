package com.libman;

// clang-format off
import java.io.InputStream;
import java.io.IOException;

import java.sql.SQLException;
import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.libman.data.Database;
import com.libman.display.WidgetDecorator;
import com.libman.display.controller.scene.DatabaseSetupScene;
// clang-format on

public class App extends Application
{
    private static Stage stage;

    public static Map<String, Object> preferences;
    public static Database database;

    @Override public void start(Stage stage) throws Exception
    {
        App.stage = stage;

        App.stage.setTitle("BS2202 - Object Oriented Software Developement - LIBMAN");
        App.stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/libman/app-icon.png")));

        try
        {
            Yaml yaml = new Yaml(new Constructor(Map.class, new LoaderOptions()));
            InputStream inputStream = getClass().getResourceAsStream("/com/libman/preferences.yml");
            App.preferences = yaml.load(inputStream);
        }
        catch (Exception exception)
        {
            Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Fatal Error");
            alert.setHeaderText("Failed load Application Preferences");

            alert.showAndWait();
            System.exit(1);
        }

        App.setRoot(getLoader("AuthenticationScene").load());

        try
        {
            App.database = new Database();
            App.database.initialize();
        }
        catch (SQLException exception)
        {
            FXMLLoader loader = getLoader("DatabaseSetupScene");
            App.setRoot(loader.load());

            DatabaseSetupScene configurator = loader.getController();
            configurator.setNote(String.format("Unable to Connect to  the Database - Please Check the Configration"));
        }
        App.stage.show();
    }

    public static FXMLLoader getLoader(String name) throws IOException
    {
        return new FXMLLoader(App.class.getResource("view/" + name + ".fxml"));
    }

    public static void setRoot(Parent node)
    {
        Scene scene = new Scene(node);
        App.stage.setScene(scene);
    }

    public static void main(String[] args)
    {
        launch();
    }
}
