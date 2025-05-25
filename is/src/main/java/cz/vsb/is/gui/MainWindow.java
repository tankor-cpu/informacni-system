package cz.vsb.is.gui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainWindow {

    public void start(Stage stage) {
        TabPane tabPane = new TabPane();

        // Вкладка "Studenti"
        Tab studentTab = new Tab("Studenti");
        studentTab.setContent(StudentTabFactory.create());
        studentTab.setClosable(false);

        // Вкладка "Kurzy"
        Tab courseTab = new Tab("Kurzy");
        courseTab.setContent(CourseTabFactory.create());
        courseTab.setClosable(false);

        // Вкладка "Zápisy"
        Tab enrollmentTab = new Tab("Zápisy");
        enrollmentTab.setContent(EnrollmentTabFactory.create());
        enrollmentTab.setClosable(false);

        // Добавление вкладок
        tabPane.getTabs().addAll(studentTab, courseTab, enrollmentTab);

        // Заголовок
        Label titleLabel = new Label("Informační systém");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-padding: 15px; -fx-text-fill: #2a2a2a;");

        // Вертикальный контейнер
        VBox root = new VBox();
        root.getChildren().addAll(titleLabel, tabPane);

        // Настройка сцены
        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Informační systém");
        stage.show();
    }
}
