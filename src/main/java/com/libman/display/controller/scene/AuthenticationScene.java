package com.libman.display.controller.scene;

// clang-format off
import com.libman.App;
import com.libman.data.Session;
import com.libman.display.controller.StageController;

import java.io.IOException;
import java.sql.SQLSyntaxErrorException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
// clang-format on

public class AuthenticationScene extends StageController
{
    @FXML private TextField username;
    @FXML private TextField password;

    @FXML private Label response;

    @FXML private void handleSubmit() throws Exception
    {
        if (this.username.getText().isEmpty())
        {
            this.response.setText("Please enter a Username");
            return;
        }

        else if (this.password.getText().isEmpty())
        {
            this.response.setText("Please enter a Password");
            return;
        }

        try
        {
            if (!Session.get().login(this.username.getText(), this.password.getText()))
            {
                this.response.setText("Incorrect Username or Password");
            }

            else
            {
                App.setRoot(App.getLoader("SessionScene").load());
            }
        }
        catch (SQLSyntaxErrorException exception)
        {
            this.response.setText("Unable to find Accounts Table");
        }
    }

    @FXML private void handleExit()
    {
        System.exit(0);
    }

    @FXML private void handleRegister() throws IOException
    {
        App.setRoot(App.getLoader("RegistrationScene").load());
    }
}
