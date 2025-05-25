package cz.vsb.is.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class EnrollmentTabFactory {

    public static BorderPane create() {
        try {
            FXMLLoader loader = new FXMLLoader(EnrollmentTabFactory.class.getResource("/fxml/enrollment_tab.fxml"));
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Nepodařilo se načíst enrollment_tab.fxml", e);
        }
    }
}
