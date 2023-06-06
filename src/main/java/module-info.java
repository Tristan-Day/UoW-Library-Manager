module com.libman {
    requires transitive javafx.graphics;
    requires transitive javafx.fxml;
    requires javafx.controls;

    requires transitive java.sql;

    requires org.yaml.snakeyaml;

    opens com.libman to javafx.fxml;
    opens com.libman.display to javafx.fxml;
    opens com.libman.display.controller to javafx.fxml;
    opens com.libman.display.controller.scene to javafx.fxml;
    opens com.libman.display.controller.activity to javafx.fxml;
    opens com.libman.display.controller.activity.inspector to javafx.fxml;
    opens com.libman.display.controller.activity.form to javafx.fxml;
    opens com.libman.display.controller.widget to javafx.fxml;

    exports com.libman;
}
