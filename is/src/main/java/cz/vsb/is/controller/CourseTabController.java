package cz.vsb.is.controller;

import cz.vsb.is.gui.CourseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CourseTabController {

    @FXML private TextField nameField;
    @FXML private TextField codeField;
    @FXML private TextField creditsField;
    @FXML private TextField searchField;

    @FXML private TableView<CourseDto> courseTable;
    @FXML private TableColumn<CourseDto, Long> idCol;
    @FXML private TableColumn<CourseDto, String> nameCol;
    @FXML private TableColumn<CourseDto, String> codeCol;
    @FXML private TableColumn<CourseDto, Integer> creditsCol;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObservableList<CourseDto> courseData = FXCollections.observableArrayList();
    private Long selectedId = null;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCode()));
        creditsCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCredits()).asObject());

        courseTable.setItems(courseData);
        courseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        courseTable.setOnMouseClicked(e -> {
            CourseDto selected = courseTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedId = selected.getId();
                nameField.setText(selected.getName());
                codeField.setText(selected.getCode());
                creditsField.setText(String.valueOf(selected.getCredits()));
            }
        });

        loadCourses();
    }

    @FXML
    private void onSearch() {
        String term = searchField.getText().toLowerCase();
        ObservableList<CourseDto> filtered = FXCollections.observableArrayList();
        for (CourseDto c : courseData) {
            if (c.getName().toLowerCase().contains(term) || c.getCode().toLowerCase().contains(term)) {
                filtered.add(c);
            }
        }
        courseTable.setItems(filtered);
    }

    @FXML
    private void onRefresh() {
        searchField.clear();
        loadCourses();
    }

    @FXML
    private void onAdd() {
        int credits;
        try {
            credits = Integer.parseInt(creditsField.getText());
        } catch (NumberFormatException e) {
            showAlert("Neplatný počet kreditů.");
            return;
        }

        String json = String.format("""
            {
                "name": "%s",
                "code": "%s",
                "credits": %d
            }
        """, nameField.getText(), codeField.getText(), credits);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/courses"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> Platform.runLater(() -> {
                    clearForm();
                    loadCourses();
                }));
    }

    @FXML
    private void onUpdate() {
        if (selectedId == null) return;

        int credits;
        try {
            credits = Integer.parseInt(creditsField.getText());
        } catch (NumberFormatException e) {
            showAlert("Neplatný počet kreditů.");
            return;
        }

        String json = String.format("""
            {
                "name": "%s",
                "code": "%s",
                "credits": %d
            }
        """, nameField.getText(), codeField.getText(), credits);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/courses/" + selectedId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> Platform.runLater(() -> {
                    clearForm();
                    loadCourses();
                }));
    }

    @FXML
    private void onDelete() {
        if (selectedId == null) return;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/courses/" + selectedId))
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> Platform.runLater(() -> {
                    clearForm();
                    loadCourses();
                }));
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    private void clearForm() {
        nameField.clear();
        codeField.clear();
        creditsField.clear();
        searchField.clear();
        selectedId = null;
        courseTable.getSelectionModel().clearSelection();
    }

    private void loadCourses() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/courses"))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> Platform.runLater(() -> {
                    courseData.clear();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode array = mapper.readTree(body);
                        for (JsonNode o : array) {
                            courseData.add(new CourseDto(
                                    o.get("id").asLong(),
                                    o.get("name").asText(),
                                    o.get("code").asText(),
                                    o.get("credits").asInt()
                            ));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Chyba");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
