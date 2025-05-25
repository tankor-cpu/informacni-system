package cz.vsb.is.controller;

import cz.vsb.is.gui.EnrollmentDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class EnrollmentTabController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> studentCombo;
    @FXML private ComboBox<String> courseCombo;

    @FXML private TableView<EnrollmentDto> enrollmentTable;
    @FXML private TableColumn<EnrollmentDto, Long> idCol;
    @FXML private TableColumn<EnrollmentDto, Long> studentIdCol;
    @FXML private TableColumn<EnrollmentDto, String> studentCol;
    @FXML private TableColumn<EnrollmentDto, String> courseCol;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObservableList<EnrollmentDto> enrollmentData = FXCollections.observableArrayList();
    private final Map<String, Long> studentMap = new HashMap<>();
    private final Map<String, Long> courseMap = new HashMap<>();
    private Long selectedId = null;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        studentIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getStudentId()).asObject());
        studentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentName()));
        courseCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseName()));

        enrollmentTable.setItems(enrollmentData);
        enrollmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        enrollmentTable.setOnMouseClicked(e -> {
            EnrollmentDto selected = enrollmentTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedId = selected.getId();
                studentCombo.setValue(selected.getStudentName());
                courseCombo.setValue(selected.getCourseName());
            }
        });

        loadEnrollments();
        loadStudents();
        loadCourses();
    }

    @FXML
    private void onSearch() {
        String term = searchField.getText().toLowerCase();
        ObservableList<EnrollmentDto> filtered = FXCollections.observableArrayList();
        for (EnrollmentDto e : enrollmentData) {
            if (e.getStudentName().toLowerCase().contains(term) ||
                    e.getCourseName().toLowerCase().contains(term)) {
                filtered.add(e);
            }
        }
        enrollmentTable.setItems(filtered);
    }

    @FXML
    private void onRefresh() {
        searchField.clear();
        enrollmentTable.setItems(enrollmentData);
        loadEnrollments();
    }

    @FXML
    private void onAdd() {
        Long studentId = studentMap.get(studentCombo.getValue());
        Long courseId = courseMap.get(courseCombo.getValue());
        if (studentId == null || courseId == null) return;

        String json = String.format("""
            {
                "studentId": %d,
                "courseId": %d
            }
            """, studentId, courseId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/enrollments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> Platform.runLater(() -> {
                    clearForm();
                    loadEnrollments();
                }));
    }

    @FXML
    private void onDelete() {
        if (selectedId == null) return;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/enrollments/" + selectedId))
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> Platform.runLater(() -> {
                    clearForm();
                    loadEnrollments();
                }));
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    @FXML
    private void onEnroll() {
        onAdd();
    }

    private void clearForm() {
        searchField.clear();
        studentCombo.setValue(null);
        courseCombo.setValue(null);
        selectedId = null;
        enrollmentTable.getSelectionModel().clearSelection();
    }

    private void loadEnrollments() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/enrollments"))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> Platform.runLater(() -> {
                    enrollmentData.clear();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode array = mapper.readTree(body);
                        for (JsonNode o : array) {
                            JsonNode student = o.get("student");
                            JsonNode course = o.get("course");

                            enrollmentData.add(new EnrollmentDto(
                                    o.get("id").asLong(),
                                    course.get("name").asText(),
                                    student.get("id").asLong(),
                                    student.get("firstName").asText() + " " + student.get("lastName").asText()
                            ));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
    }

    private void loadStudents() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/students"))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> Platform.runLater(() -> {
                    studentMap.clear();
                    studentCombo.getItems().clear();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode array = mapper.readTree(body);
                        for (JsonNode o : array) {
                            String name = o.get("firstName").asText() + " " + o.get("lastName").asText();
                            Long id = o.get("id").asLong();
                            studentMap.put(name, id);
                            studentCombo.getItems().add(name);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
    }

    private void loadCourses() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/courses"))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> Platform.runLater(() -> {
                    courseMap.clear();
                    courseCombo.getItems().clear();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode array = mapper.readTree(body);
                        for (JsonNode o : array) {
                            String name = o.get("name").asText();
                            Long id = o.get("id").asLong();
                            courseMap.put(name, id);
                            courseCombo.getItems().add(name);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
    }
}
