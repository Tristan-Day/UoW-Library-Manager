package com.libman.display.controller.activity.form;

// clang-format off
import com.libman.data.model.Entity;
import com.libman.data.model.Patron;
import com.libman.display.WidgetDecorator;
import com.libman.display.controller.activity.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.sql.SQLIntegrityConstraintViolationException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
// clang-format on

public class PatronRegistrationForm extends Activity
{
    @FXML private TextField firstname;
    @FXML private TextField lastname;
    @FXML private TextField mobile;
    @FXML private TextField email;
    @FXML private TextField postcode;

    @FXML private void handleSubmit() throws Exception
    {
        Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Patron Registration");

        List<TextField> fields =
            new ArrayList<>(Arrays.asList(this.firstname, this.lastname, this.mobile, this.email, this.postcode));

        // Check for empty fields
        for (TextField field : fields)
        {
            if (field.getText().isEmpty())
            {
                alert.setHeaderText(String.format("%s Cannot be Empty", field.getPromptText()));
                alert.setContentText(String.format("Please enter a valid %s", field.getPromptText()));

                alert.show();
                return;
            }
        }

        // Validate fields
        if (this.firstname.getText().matches(".*[1-9].*") || this.lastname.getText().matches(".*[1-9].*"))
        {
            alert.setHeaderText("Patron Name Cannot Contain Numbers");
            alert.setContentText("Please enter only alphabetical characters.");

            alert.show();
            return;
        }

        if (this.mobile.getText().matches(".*[A-Za-z].*"))
        {
            alert.setHeaderText("Mobile Number Cannot Contain Letters");
            alert.setContentText("Please enter only numeric characters.");

            alert.show();
            return;
        }

        if (this.mobile.getText().length() != 11)
        {
            alert.setHeaderText("Mobile Number Must Contain Eleven Digits");
            alert.setContentText("Please enter a mobile number containing 11 digits.");

            alert.show();
            return;
        }

        if (!this.email.getText().matches(".*[@].*"))
        {
            alert.setHeaderText("Email Address Must Contain an Address Sign");
            alert.setContentText("Please enter an address including a valid address sign");

            alert.show();
            return;
        }

        if (this.postcode.getText().length() > 8)
        {
            alert.setHeaderText("Postcode Cannot Exceed 8 Characters");
            alert.setContentText("Please enter a maximum of 8 alphanumeric characters.");

            alert.show();
            return;
        }

        // Register the patron
        try
        {
            Patron patron = Entity.createBlank(Patron.class);

            patron.firstname = this.firstname.getText();
            patron.lastname = this.lastname.getText();
            patron.mobile = this.mobile.getText();
            patron.email = this.email.getText();
            patron.postcode = this.postcode.getText();

            patron.create();

            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Patron Added Sucessfully");
            alert.setContentText(String.format("Patron ID Number: %d", patron.identifier.getValue()));

            alert.show();
        }
        catch (SQLIntegrityConstraintViolationException exception)
        {
            alert.setHeaderText("Some Fields are not Unqiue");
            alert.setContentText("Mobile Number, Email Address and Postcode must be unique.");

            alert.show();
            return;
        }
        catch (Exception exception)
        {
            alert.setHeaderText("Failed to Register new Patron");
            alert.setContentText(exception.getMessage());

            alert.show();
            return;
        }

        // Reset the form
        for (TextField field : fields)
        {
            field.setText("");
        }
    }
}
