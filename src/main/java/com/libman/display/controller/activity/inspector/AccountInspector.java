package com.libman.display.controller.activity.inspector;

// clang-format off
import com.libman.data.model.Account;
import com.libman.display.WidgetDecorator;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
// clang-format on

public class AccountInspector extends InspectorStrategy
{
    @FXML private Label name;
    @FXML private Label username;
    @FXML private Label type;

    @FXML private PasswordField password;

    @FXML private Button buttonResetPassword;
    @FXML private Button buttonDelete;

    private Account account;

    @Override public void setTargetObject(Object target) throws Exception
    {
        this.account = (Account)target;

        // Set account details
        this.name.setText(this.account.firstname);
        this.username.setText(this.account.username);

        this.type.setText(String.format("(%s)", this.account.type.name));
        this.password.setText(this.account.password);

        this.buttonDelete.setDisable(false);
        this.password.setDisable(false);
        this.buttonResetPassword.setDisable(false);
    }

    @FXML private void handleResetPassword() throws Exception
    {
        this.account.password = this.password.getText();
        this.account.update();

        Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.INFORMATION), "Account Management");
        alert.setHeaderText("Password Sucessfully Changed");
        alert.setContentText(String.format("New Password: \n%s", this.account.password));
        alert.show();
    }

    @FXML private void handleDeleteUser() throws Exception
    {
        this.account.delete();

        // Generate confirmation dialog
        Alert dialog = WidgetDecorator.style(new Alert(Alert.AlertType.CONFIRMATION), "Account Management");
        dialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
        dialog.setHeaderText(String.format("Are you sure you want to Delete User \n%s?", this.account.username));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES)
        {
            this.account.delete();

            // Generate deletion feedback
            Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.INFORMATION), "Account Management");
            alert.setHeaderText("User Sucessfully Deleted");
            alert.show();

            // Disable interaction
            this.buttonDelete.setDisable(true);
            this.password.setDisable(true);
            this.buttonResetPassword.setDisable(true);
        }
    }
}
