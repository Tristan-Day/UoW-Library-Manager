package com.libman.display.controller.activity.inspector;

// clang-format off
import com.libman.data.model.Account;
import com.libman.display.WidgetDecorator;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
// clang-format on

public class PendingAccountInspector extends InspectorStrategy
{
    @FXML private Label name;
    @FXML private Label username;

    @FXML private Label type;

    @FXML private Button buttonApprove;
    @FXML private Button buttonReject;

    private Account account;

    @Override public void setTargetObject(Object target) throws Exception
    {
        this.account = (Account)target;

        // Set account details
        this.name.setText(this.account.firstname);
        this.username.setText(this.account.username);
        this.type.setText(this.account.type.name);

        this.buttonApprove.setDisable(false);
        this.buttonReject.setDisable(false);
    }

    @FXML private void handleApprove() throws SQLException
    {
        this.account.approve();

        // Generate approval feedback
        Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.INFORMATION), "Account Management");
        alert.setHeaderText("User Request Approved");
        alert.show();

        // Disable buttons
        this.buttonApprove.setDisable(true);
        this.buttonReject.setDisable(true);
    }

    @FXML private void handleReject() throws SQLException
    {
        this.account.reject();

        // Generate rejection feedback
        Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.INFORMATION), "Account Management");
        alert.setHeaderText("User Request Rejected");
        alert.show();

        // Disable buttons
        this.buttonApprove.setDisable(true);
        this.buttonReject.setDisable(true);
    }
}
