package com.libman.display.controller.activity.form;

// clang-format off
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.sql.Blob;
import java.text.NumberFormat;

import java.util.Locale;

import com.libman.App;
import com.libman.data.model.Entity;
import com.libman.data.model.Resource;
import com.libman.data.model.ResourceType;
import com.libman.data.model.TagType;

import com.libman.display.WidgetDecorator;
import com.libman.display.controller.activity.Activity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.util.Pair;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
// clang-format on

public class ResourceEditorForm extends Activity
{
    @FXML private Label subtitle;

    @FXML private TextField resourceName;

    @FXML private ComboBox<ResourceType> resourceParentType;
    @FXML private ComboBox<ResourceType> resourceSubtype;
    @FXML private Spinner<Integer> resourceCopies;

    @FXML private ListView<Pair<TagType, String>> resourceTags;
    @FXML private TextField tagName;
    @FXML private TextField tagValue;

    @FXML private Button addTagButton;
    @FXML private Button deleteTagButton;

    @FXML private ImageView resourcePreview;
    @FXML private TextField resourceLateFee;

    @FXML private Button submitButton;

    private Resource resource;

    public void initalise()
    {
        this.resourceCopies.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        this.resourceTags.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.resourceTags.setCellFactory(listView -> new ListCell<Pair<TagType, String>>() {
            @Override protected void updateItem(Pair<TagType, String> item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty || item == null)
                {
                    setText(null);
                }
                else
                {
                    setText(String.format("%s : %s", item.getKey().name.toUpperCase(), item.getValue().toUpperCase()));
                }
            }
        });
    }

    public void setTargetObject(Object object) throws Exception
    {
        this.reset();
        this.resource = (Resource)object;

        // Using the controller in edit mode
        if (this.resource != null)
        {
            this.subtitle.setText("Resource Editor");
            this.submitButton.setText("UPDATE RESOURCE");

            this.resourceName.setText(this.resource.name);

            ResourceType inheritedType = Entity.createBlank(ResourceType.class);
            inheritedType.name = "Inherited";

            if (this.resource.type.getParent() != null)
            {
                this.resourceParentType.getItems().addAll(this.resource.type.getParent().getPeers());
                this.resourceParentType.getItems().add(this.resource.type.getParent());
                this.resourceParentType.getSelectionModel().select(this.resource.type.getParent());

                this.resourceSubtype.getItems().addAll(this.resource.type.getPeers());
                this.resourceSubtype.getItems().add(this.resource.type);
                this.resourceSubtype.getSelectionModel().select(this.resource.type);
            }
            else
            {
                // Case where subtype is inherited
                this.resourceParentType.getItems().addAll(this.resource.type.getPeers());
                this.resourceParentType.getItems().add(this.resource.type);
                this.resourceParentType.getSelectionModel().select(this.resource.type);

                this.resourceSubtype.getItems().addAll(this.resourceParentType.getValue().getChildren());
                this.resourceSubtype.getItems().add(inheritedType);
                this.resourceSubtype.getSelectionModel().select(inheritedType);
            }

            this.resourceCopies.getValueFactory().setValue(this.resource.copies);

            ObservableList<Pair<TagType, String>> tags = FXCollections.observableArrayList(this.resource.tags);
            this.resourceTags.setItems(tags);

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

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
            this.resourceLateFee.setText(currencyFormat.format(this.resource.type.getLateFee()));
        }

        // When creating a new resource
        else
        {
            this.resource = Entity.createBlank(Resource.class);

            this.subtitle.setText("Create New Resource");
            this.submitButton.setText("CREATE RESOURCE");

            this.resourceParentType.getItems().addAll(ResourceType.getRootTypes());
        }
    }

    private void reset() throws Exception
    {
        this.resourceName.setText("");

        this.resourceParentType.getItems().clear();
        this.resourceSubtype.getItems().clear();
        this.resourceCopies.getValueFactory().setValue(1);

        this.resourceTags.getItems().clear();
        this.tagName.setText("");
        this.tagValue.setText("");

        Blob imageBlob = Resource.getDefaultImage();
        Image image = new Image(new ByteArrayInputStream(imageBlob.getBytes(1, (int)imageBlob.length())));
        this.resourcePreview.setImage(image);

        this.resourceLateFee.setText("");

        this.addTagButton.setDisable(false);
        this.deleteTagButton.setDisable(true);
    }

    @FXML private void handleParentTypeSelection() throws Exception
    {
        if (this.resourceParentType.isFocused())
        {
            this.resourceSubtype.getItems().clear();

            ResourceType type = Entity.createBlank(ResourceType.class);
            type.name = "Inherited";

            this.resourceSubtype.getItems().add(type);
            this.resourceSubtype.getItems().addAll(this.resourceParentType.getValue().getChildren());

            this.resourceSubtype.setValue(type);
            this.resource.type = this.resourceParentType.getSelectionModel().getSelectedItem();

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
            this.resourceLateFee.setText(currencyFormat.format(this.resource.type.getLateFee()));
        }
    }

    @FXML private void handleSubtypeSelection() throws Exception
    {
        if (this.resourceSubtype.isFocused())
        {
            this.resource.type = this.resourceSubtype.getSelectionModel().getSelectedItem();

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.UK);
            this.resourceLateFee.setText(currencyFormat.format(this.resource.type.getLateFee()));
        }
    }

    @FXML private void handleTagSelection()
    {
        if (this.resourceTags.getSelectionModel().isEmpty())
        {
            this.deleteTagButton.setDisable(true);
        }
        else
        {
            this.deleteTagButton.setDisable(false);
        }
    }

    @FXML private void handleAddTag() throws Exception
    {
        if (this.tagName.getText().isEmpty())
        {
            Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Resource Editor");
            alert.setHeaderText("Cannot add Tag with Empty Name");
            alert.setContentText("Please enter a Tag Name");

            alert.show();
            return;
        }

        if (this.tagValue.getText().isEmpty())
        {
            Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Resource Editor");
            alert.setHeaderText("Cannot add Tag with Empty Value");
            alert.setContentText("Please enter a Tag Value");

            alert.show();
            return;
        }

        TagType tagType;

        try
        {
            // Case where the tag already exists
            tagType = TagType.fromName(this.tagName.getText().toLowerCase());
        }
        catch (Exception exception)
        {
            // Case where the tag does not already exist
            tagType = Entity.createBlank(TagType.class);
            tagType.name = this.tagName.getText();
        }

        this.resource.tags.add(new Pair<>(tagType, this.tagValue.getText().toLowerCase()));

        ObservableList<Pair<TagType, String>> tags = FXCollections.observableArrayList(this.resource.tags);
        this.resourceTags.setItems(tags);

        this.tagName.setText("");
        this.tagValue.setText("");
    }

    @FXML private void handleDeleteTag() throws Exception
    {
        this.resource.tags.remove(this.resourceTags.getSelectionModel().getSelectedIndex());

        ObservableList<Pair<TagType, String>> tags = FXCollections.observableArrayList(this.resource.tags);
        this.resourceTags.setItems(tags);
    }

    @FXML private void handleAssignPreview() throws Exception
    {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select Resource Thumbnail");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png"));

        File selection = fileChooser.showOpenDialog(new Stage());
        if (selection != null)
        {
            try (FileInputStream file = new FileInputStream(selection))
            {
                byte[] imageBytes = file.readAllBytes();

                // Check the file does not exceed the size limit for MediumBlob
                if (imageBytes.length > 16 * 1024 * 1024)
                {
                    Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Resource Editor");
                    alert.setHeaderText("Image is too large");
                    alert.setContentText("Please select an image under 16MB");

                    alert.show();
                    return;
                }

                this.resourcePreview.setImage(new Image(new ByteArrayInputStream(imageBytes)));

                if (this.resource.image == null)
                {
                    this.resource.image = App.database.connection.createBlob();
                }
                this.resource.image.setBytes(1, imageBytes);
            }
            catch (IOException exception)
            {
                Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Resource Editor");
                alert.setHeaderText("Failed to Read Image Data");

                alert.show();
            }
        }
    }

    @FXML private void handleSubmit() throws Exception
    {
        Alert alert = WidgetDecorator.style(new Alert(Alert.AlertType.ERROR), "Resource Editor");

        if (this.resourceName.getText().isEmpty())
        {
            alert.setHeaderText("Resource name must not be Empty");
            alert.setContentText("Please enter a valid resoruce name");

            alert.show();
            return;
        }

        if (this.resourceParentType.getSelectionModel().isEmpty() || this.resourceSubtype.getSelectionModel().isEmpty())
        {
            alert.setHeaderText("Resource type must be set");
            alert.setContentText("Please select a valid resource type");

            alert.show();
            return;
        }

        this.resource.name = this.resourceName.getText();

        // Determine and set resource type
        if (this.resourceSubtype.getSelectionModel().getSelectedItem().name == "Inherited")
        {
            this.resource.type = this.resourceParentType.getSelectionModel().getSelectedItem();
        }
        else
        {
            this.resource.type = this.resourceSubtype.getSelectionModel().getSelectedItem();
        }

        // Set resource copies
        this.resource.copies = this.resourceCopies.getValue();

        // Write to the database and show feedback
        alert.setAlertType(Alert.AlertType.INFORMATION);
        if (this.resource.identifier.getValue() != null)
        {
            this.resource.update();
            alert.setHeaderText("Resource Sucessfully Updated");
        }
        else
        {
            this.resource.create();
            alert.setHeaderText("Resource Sucessfully Created");

            this.reset();
        }
        alert.show();
    }
}
