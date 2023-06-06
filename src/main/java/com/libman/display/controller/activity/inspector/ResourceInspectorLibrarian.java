package com.libman.display.controller.activity.inspector;

// clang-format off
import com.libman.data.model.Resource;
import com.libman.data.model.ResourceType;
import com.libman.data.model.TagType;

import com.libman.display.WidgetDecorator;
import com.libman.display.controller.widget.TagWidget;
import com.libman.App;

import com.libman.data.DocumentBuilder;

import com.libman.data.model.Entity;
import com.libman.data.model.Loan;
import com.libman.data.model.Patron;
import com.libman.data.model.Reservation;

import java.sql.Blob;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.util.Pair;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
// clang-format on

public class ResourceInspectorLibrarian extends InspectorStrategy
{
    // Resource details
    @FXML private Label resourceName;
    @FXML private Label resourceType;

    @FXML private Label resourceCopies;
    @FXML private Label resourceCopiesAvailable;
    @FXML private Label resourceCopiesUnavailable;
    @FXML private Label resourceNextReturnDate;

    @FXML private ImageView resourcePreview;

    @FXML private Label resourceTagsHeading;
    @FXML private HBox resourceTags;

    private Resource resource;

    // Loan / Reservation issuement
    @FXML private Label issueType;

    @FXML private TextField patronIdentifier;
    @FXML private Label patronSearchResponse;

    @FXML private Label patronName;
    @FXML private Label patronMobile;
    @FXML private Label patronEmail;
    @FXML private Label patronPostcode;

    @FXML private Button searchButton;
    @FXML private Button issueButton;

    private Patron patron;

    @Override public void setTargetObject(Object target) throws Exception
    {
        this.resource = (Resource)target;
        this.patron = null;

        this.reset();

        this.resourceName.setText(this.resource.name);
        
        ResourceType parentType = this.resource.type.getParent();
        if (parentType != null)
        {
            this.resourceType.setText(this.resource.type.name.toUpperCase() + "  |  " + parentType.name.toUpperCase());
        }
        else
        {
            this.resourceType.setText(this.resource.type.name.toUpperCase());
        }

        this.resourceCopies.setText(String.valueOf(this.resource.copies));
        this.resourceCopiesAvailable.setText(String.valueOf(this.resource.getAvailableCopies()));
        this.resourceCopiesUnavailable.setText(String.valueOf(this.resource.getUnavailableCopies()));

        LocalDate nextReturnDate = this.resource.getNextReturnDate();
        this.resourceNextReturnDate.setText(nextReturnDate != null ? nextReturnDate.toString() : "N/A");

        // Set resource image
        Blob imageBlob;
        if (this.resource.image != null)
        {
            imageBlob = this.resource.image;
        }
        else
        {
            imageBlob = Resource.getDefaultImage();
        }
        Image image = new Image(new ByteArrayInputStream(imageBlob.getBytes(1, (int)imageBlob.length())));
        this.resourcePreview.setImage(image);

        // Create and set tags
        this.resourceTags.getChildren().clear();

        if (!resource.tags.isEmpty())
        {
            this.resourceTagsHeading.setVisible(true);

            for (Pair<TagType, String> tag : resource.tags)
            {
                FXMLLoader loader = App.getLoader("widget/TagWidget");
                this.resourceTags.getChildren().add(loader.load());

                TagWidget widget = loader.getController();
                widget.value.setText(
                    String.format("%s : %s", tag.getKey().name.toUpperCase(), tag.getValue().toUpperCase()));
            }
        }
        else
        {
            this.resourceTagsHeading.setVisible(false);
        }

        // Determine issuement type
        if (resource.hasReservation())
        {
            Reservation reservation = Reservation.fromResource(this.resource);

            if (reservation.hasExpired())
            {
                reservation.delete();
            }
            else
            {
                this.patronIdentifier.setText(String.valueOf(reservation.patron.identifier.getValue()));
                this.handlePatronSearch();

                this.issueType.setText("Current Reservation");
                this.issueButton.setText("ISSUE LOAN");

                this.patronIdentifier.setEditable(false);
                this.searchButton.setDisable(true);

                if (this.resource.getAvailableCopies() < 1)
                {
                    this.issueButton.setDisable(true);
                }
            }
        }

        else if (resource.getAvailableCopies() != 0)
        {
            this.issueType.setText("Issue Loan");
            this.issueButton.setText("ISSUE LOAN");
        }

        else
        {
            this.issueType.setText("Issue Reservation");
            this.issueButton.setText("ISSUE RESERVATION");
        }
    }

    private void reset()
    {
        this.patronIdentifier.clear();
        this.patronIdentifier.setEditable(true);
        
        this.patronSearchResponse.setText("");
        this.searchButton.setDisable(false);
    
        this.patronName.setText("");
        this.patronMobile.setText("");
        this.patronEmail.setText("");
        this.patronPostcode.setText("");

        this.issueButton.setDisable(false);
    }

    @FXML private void handlePatronSearch() throws Exception
    {
        // Validate search fields
        if (this.patronIdentifier.getText().isEmpty())
        {
            this.patronSearchResponse.setText("Please enter a Patron ID");
            return;
        }

        if (this.patronIdentifier.getText().matches("[A-Za-z]+"))
        {
            this.patronSearchResponse.setText("Please enter a Patron ID");
            return;
        }

        try
        {
            this.patron = Entity.fromIdentifier(Patron.class, Integer.parseInt(this.patronIdentifier.getText()));

            String patronName = this.patron.firstname + " " + this.patron.lastname;
            this.patronName.setText(patronName);

            this.patronMobile.setText(this.patron.mobile);
            this.patronEmail.setText(this.patron.email);
            this.patronPostcode.setText(this.patron.postcode);
        }
        catch (Exception exception)
        {
            this.patronSearchResponse.setText("Patron not Found");
        }
    }

    @FXML private void handleIssue() throws Exception
    {
        if (this.patron == null)
        {
            Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Resource Management");
            alert.setHeaderText("Submission Error");
            alert.setContentText("No Patron Selected");

            alert.show();
            return;
        }

        if (this.resource.getAvailableCopies() != 0 || this.resource.hasReservation())
        {
            if (this.resource.hasReservation())
            {
                Reservation reservation = Reservation.fromResource(this.resource);
                reservation.delete();
            }

            // Isssue a loan
            Loan loan = this.resource.loan(this.patron);
            loan.create();

            // Create issuement receipt
            String filename = String.format(
                "Issuement Receipt - %s %s - %s", loan.patron.firstname, loan.patron.lastname, loan.resource.name);
            DocumentBuilder.exportDocument(DocumentBuilder.generateTakeoutReceipt(loan), filename);

            Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.INFORMATION), "Resource Management");
            alert.setHeaderText("Loan Issued Sucessfully");
            alert.setContentText(String.format("Due Date: %s\n\nReceipt exported under Documents Folder", loan.getDueDate()));
            alert.show();
        }
        else
        {
            // Issue a reservation
            Reservation reservation = this.resource.reserve(this.patron);

            Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.INFORMATION), "Resource Management");
            alert.setHeaderText("Reservation Issued Sucessfully");
            alert.setContentText(String.format("Valid Until: %s", reservation.getExpriyDate().toString()));
            alert.show();
        }

        this.setTargetObject(this.resource);
    }
}
