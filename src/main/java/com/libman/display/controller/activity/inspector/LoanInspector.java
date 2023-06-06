package com.libman.display.controller.activity.inspector;

// clang-format off
import com.libman.App;

import com.libman.data.DocumentBuilder;

import com.libman.display.WidgetDecorator;
import com.libman.data.model.Loan;
import com.libman.data.model.LoanState;
import com.libman.data.model.Reservation;
import com.libman.data.model.Resource;

import java.sql.Blob;
import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoanInspector extends InspectorStrategy
{
    @FXML private Label resourceName;
    @FXML private Label resouceType;

    @FXML private Label loanStatus;
    @FXML private Label loanStatusDate;

    @FXML private Label patronName;
    @FXML private Label patronMobile;
    @FXML private Label patronEmail;
    @FXML private Label patronPostcode;

    @FXML private ImageView resourcePreview;
    @FXML private Label loanLateFee;

    @FXML private Button renewButton;
    @FXML private Button returnButton;

    private Loan loan;

    @Override public void setTargetObject(Object target) throws Exception
    {
        this.loan = (Loan)target;

        // Set resource details
        this.resourceName.setText(this.loan.resource.name);
        this.resouceType.setText(this.loan.resource.type.name.toUpperCase());

        this.loanStatus.setText(this.loan.getState().toString().toUpperCase());
        this.loanStatusDate.setText(this.loan.dateIssued.toString());

        // Set resource image
        Blob imageBlob;
        if (this.loan.resource.image != null)
        {
            imageBlob = this.loan.resource.image;
        }
        else
        {
            imageBlob = Resource.getDefaultImage();
        }
        Image image = new Image(new ByteArrayInputStream(imageBlob.getBytes(1, (int)imageBlob.length())));
        this.resourcePreview.setImage(image);

        // Set patron details
        String patronName = this.loan.patron.firstname + " " + this.loan.patron.lastname;
        this.patronName.setText(patronName);

        this.patronMobile.setText(loan.patron.mobile);
        this.patronEmail.setText(loan.patron.email);
        this.patronPostcode.setText(loan.patron.postcode);

        // Set late fee
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
        String lateFee = currencyFormat.format(this.loan.getLateFee());
        this.loanLateFee.setText(String.format("%s (%d Days Late)", lateFee, this.loan.getDaysLate()));

        // Re-enable disabled buttons
        this.returnButton.setDisable(false);
        this.renewButton.setDisable(false);

        // Check if the loan is overdue
        if (this.loan.getState() == LoanState.OVERDUE)
        {
            this.renewButton.setDisable(true);
            this.loanStatusDate.setText(this.loan.getDueDate().toString());
        }

        // Check if the loan can be renewed
        else if (this.loan.resource.hasReservation())
        {
            Reservation reservation = Reservation.fromResource(this.loan.resource);

            if (reservation.hasExpired())
            {
                reservation.delete();
            }
            else
            {
                this.renewButton.setDisable(true);
            }
        }
    }

    @FXML private void handleResourceRenew() throws Exception
    {
        this.loan.dateIssued = LocalDate.now();
        this.loan.update();

        // Create renewel feedback
        Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.INFORMATION), "Circulation Management");
        alert.setHeaderText("Resource Renewed Sucessfully");

        StringBuilder message = new StringBuilder(String.format("%s (%s)\n", this.loan.resource.name, this.loan.resource.type.name));

        message.append(String.format("\n(For an Additional %d Days)\n", App.preferences.get("loan-period")));
        message.append(String.format("\nNew Due Date: %s", this.loan.getDueDate().toString()));

        alert.setContentText(message.toString());
        alert.show();

        // Refresh loan status
        this.loanStatusDate.setText(this.loan.dateIssued.toString());
    }

    @FXML private void handleResourceReturn() throws Exception
    {
        this.loan.dateReturned = LocalDate.now();
        this.loan.update();

        // Generate return receipt
        String filename = String.format(
            "Return Receipt - %s %s - %s", this.loan.patron.firstname, this.loan.patron.lastname, this.loan.resource.name);
        DocumentBuilder.exportDocument(DocumentBuilder.generateReturnReceipt(loan), filename);

        // Generate return feedback
        Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.INFORMATION), "Circulation Management");
        alert.setHeaderText("Resource Returned Sucessfully");

        StringBuilder message = new StringBuilder(String.format("%s (%s)\n", this.loan.resource.name, this.loan.resource.type.name));

        if (this.loan.getState() == LoanState.OVERDUE)
        {
            message.append(String.format("\nLate Fees: %s\n", this.loanLateFee.getText()));
        }

        message.append("\nReceipt exported as:\n");
        message.append(filename);

        alert.setContentText(message.toString());
        alert.show();

        // Disable buttons and update UI
        this.renewButton.setDisable(true);
        this.returnButton.setDisable(true);

        this.loanStatus.setText(this.loan.getState().toString().toUpperCase());
        this.loanStatusDate.setText(this.loan.dateReturned.toString());
    }
}
