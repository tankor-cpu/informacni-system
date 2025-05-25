package cz.vsb.is.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class StudentTabFactory {

    public static BorderPane create() {
        try {
            FXMLLoader loader = new FXMLLoader(StudentTabFactory.class.getResource("/fxml/student_tab.fxml"));
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Nepodařilo se načíst student_tab.fxml", e);
        }
    }
}
