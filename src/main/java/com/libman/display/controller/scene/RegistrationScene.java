package com.libman.display.controller.scene;

// clang-format off
import com.libman.App;
import com.libman.data.model.Account;
import com.libman.data.model.AccountType;
import com.libman.data.model.Entity;
import com.libman.display.WidgetDecorator;
import com.libman.display.controller.StageController;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
// clang-format on

public class RegistrationScene extends StageController
{
    @FXML private TextField firstname;
    @FXML private ComboBox<AccountType> accountType;

    @FXML private TextField username;
    @FXML private TextField password;

    @FXML private Label strength;

    private String getPasswordStrength()
    {
        String password = this.password.getText();
        Integer strength = 0;

        if (password.length() > 8)
        {
            strength++;

            if (password.length() > 11)
            {
                strength++;
            }
        }
        else
        {
            strength--;
        }

        // Use a regular expression to check for uppercase and lowercase letters
        if (password.matches(".*[A-Z].*") && password.matches(".*[a-z].*"))
        {
            strength++;
        }
        else
        {
            strength--;
        }

        // Use a regular expression to check for numbers
        if (password.matches(".*[1-9].*"))
        {
            strength++;
        }
        else
        {
            strength--;
        }

        switch (strength)
        {
        case 4:
            return "Excellent";

        case 3:
            return "Good";

        case 2:
            return "Weak";

        default:
            return "Very Weak";
        }
    }

    @Override protected void show() throws Exception
    {
        for (AccountType type : AccountType.getAccountTypes())
        {
            this.accountType.getItems().add(type);
        }
    }

    @FXML private void handleStrengthCheck()
    {
        this.strength.setText(String.format("Strength: %s", this.getPasswordStrength()));
    }

    @FXML private void handleReturn() throws IOException
    {
        App.setRoot(App.getLoader("AuthenticationScene").load());
    }

    @FXML private void handleSubmit()
    {
        Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Account Registration");

        // Field validation
        if (this.firstname.getText().isEmpty())
        {
            alert.setContentText("Please enter a valid account name.");
            alert.show();
        }

        else if (this.username.getText().isEmpty())
        {
            alert.setContentText("Please enter a valid username.");
            alert.show();
        }

        else if (this.accountType.getValue() == null)
        {
            alert.setContentText("Please select an account type.");
            alert.show();
        }

        else if (!this.getPasswordStrength().matches(".*\\b(Good|Excellent)\\b.*"))
        {
            String password = this.password.getText();

            if (password.length() < 8)
            {
                alert.setHeaderText("Password is too Short");
                alert.setContentText("Please enter a password with at least eight characters.");
            }

            else if (!password.matches(".*[a-z].*"))
            {
                alert.setHeaderText("Password does not contain a Lowercase Letter");
                alert.setContentText("Please enter a password with at least one lowercase letter.");
            }

            else if (!password.matches(".*[A-Z].*"))
            {
                alert.setHeaderText("Password does not contain an Uppercase Letter");
                alert.setContentText("Please enter a password with at least one uppercase letter.");
            }

            else if (!password.matches(".*[1-9].*"))
            {
                alert.setHeaderText("Password does not contain a Number");
                alert.setContentText("Please enter a password with at least one number.");
            }

            alert.setTitle("Weak Password");
            alert.show();
        }

        else
        {
            try
            {
                // Write the new account to the database
                Account account = Entity.createBlank(Account.class);

                account.type = ((AccountType)this.accountType.getValue());

                account.firstname = this.firstname.getText();
                account.username = this.username.getText();
                account.password = this.password.getText();

                account.create();
                account.setPending();

                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("LIBMAN - Registration");
                alert.setHeaderText("Registration Request Submitted");

                alert.showAndWait();
                this.handleReturn();
            }
            catch (Exception exception)
            {
                // Handle the case when the username is not unique
                if (exception instanceof SQLIntegrityConstraintViolationException)
                {
                    alert.setHeaderText("Username already taken");
                    alert.setContentText("Please enter a unique username.");
                }
                else
                {
                    alert.setHeaderText("Failed to Register new User");
                    alert.setContentText(exception.getMessage());
                }
                alert.show();
            }
        }
    }
}