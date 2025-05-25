package cz.vsb.is.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class CourseTabFactory {

    public static BorderPane create() {
        try {
            FXMLLoader loader = new FXMLLoader(CourseTabFactory.class.getResource("/fxml/course_tab.fxml"));
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Nepodařilo se načíst course_tab.fxml", e);
        }
    }
}
