package com.libman.display.controller.activity.inspector;

// clang-format off
import com.libman.App;
import com.libman.data.DocumentBuilder;
import com.libman.data.model.Resource;
import com.libman.data.model.ResourceType;
import com.libman.data.model.TagType;

import com.libman.display.WidgetDecorator;
import com.libman.display.controller.activity.form.ResourceEditorForm;
import com.libman.display.controller.widget.TagWidget;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.SQLException;

import java.time.LocalDate;
import java.util.Optional;

import javafx.util.Pair;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
// clang-format on

public class ResourceInspectorCurator extends InspectorStrategy
{
    @FXML private Label resourceName;
    @FXML private Label resourceType;

    @FXML private Label resourceCopies;
    @FXML private Label resourceCopiesAvailable;
    @FXML private Label resourceCopiesUnavailable;
    @FXML private Label resourceNextReturnDate;

    @FXML private ImageView resourcePreview;

    @FXML private Label resourceTagsHeading;
    @FXML private HBox resourceTags;

    @FXML private Button editResourceButton;
    @FXML private Button generateReportButton;

    @FXML private Button deleteResourceButton;

    private ResourceEditorForm resourceEditor;
    private Resource resource;

    @Override public void initalise() throws Exception
    {
        FXMLLoader loader = App.getLoader("activity/form/ResourceEditorForm");

        StackPane stack = (StackPane)this.root;
        stack.getChildren().add(loader.load());

        this.resourceEditor = loader.getController();

        this.resourceEditor.setParent(stack.getChildren().get(0));
        this.resourceEditor.initalise();
    }

    @Override public void setTargetObject(Object target) throws Exception
    {
        this.resource = (Resource)target;
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
    }

    private void reset()
    {
        this.resourceEditor.hide();

        this.editResourceButton.setDisable(false);
        this.generateReportButton.setDisable(false);
        this.deleteResourceButton.setDisable(false);
    }

    @FXML private void handleEditResource() throws Exception
    {
        this.resourceEditor.setTargetObject(this.resource);
        this.resourceEditor.show();
    }

    @FXML private void handleGenerateReport() throws Exception
    {
        // Create resource report
        String filename = String.format("Resource Report - %s - %s", this.resource.name, LocalDate.now().toString());
        DocumentBuilder.exportDocument(DocumentBuilder.generateResourceReport(this.resource), filename);

        // Generate export feedback
        Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.INFORMATION), "Resource Management");
        alert.setHeaderText("Resource Report Exported Sucessfully");
        alert.setContentText(String.format("Exported as: %s", filename));
        alert.show();
    }

    @FXML private void handleDeleteResource() throws SQLException
    {
        Alert dialog = WidgetDecorator.style(new Alert(Alert.AlertType.CONFIRMATION), "Resource Management");

        dialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
        dialog.setHeaderText(String.format("Are you sure you want to delete %s?", this.resource.name));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES)
        {
            this.resource.delete();

            this.editResourceButton.setDisable(true);
            this.generateReportButton.setDisable(true);
            this.deleteResourceButton.setDisable(true);
        }
    }
}
