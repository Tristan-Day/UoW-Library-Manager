package com.libman.display;

// clang-format off
import com.libman.App;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
// clang-format on

public class WidgetDecorator
{
    public static Alert style(Alert dialog, String title)
    {
        dialog.setTitle(String.format("LIBMAN - %s", title));

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(App.class.getResource("stylesheet.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-plane");

        return dialog;
    }
}
