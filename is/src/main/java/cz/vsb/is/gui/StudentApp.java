package cz.vsb.is.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class StudentApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        new MainWindow().start(stage);
    }
}
