package cz.vsb.is.controller;

import cz.vsb.is.gui.StudentDto;
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

public class StudentTabController {

    @FXML private TextField searchField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField dobField;
    @FXML private ComboBox<String> genderBox;
    @FXML private TextField phoneField;

    @FXML private TableView<StudentDto> studentTable;
    @FXML private TableColumn<StudentDto, Long> idCol;
    @FXML private TableColumn<StudentDto, String> firstNameCol;
    @FXML private TableColumn<StudentDto, String> lastNameCol;
    @FXML private TableColumn<StudentDto, String> emailCol;
    @FXML private TableColumn<StudentDto, String> dobCol;
    @FXML private TableColumn<StudentDto, String> genderCol;
    @FXML private TableColumn<StudentDto, String> phoneCol;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObservableList<StudentDto> studentData = FXCollections.observableArrayList();
    private Long selectedId = null;

    @FXML
    public void initialize() {
        genderBox.getItems().addAll("Muž", "Žena", "Jiné");

        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        firstNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        lastNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        dobCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDateOfBirth()));
        genderCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGender()));
        phoneCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        studentTable.setItems(studentData);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        studentTable.setRowFactory(tv -> {
            TableRow<StudentDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() >= 1) {
                    StudentDto selected = row.getItem();
                    selectedId = selected.getId();
                    firstNameField.setText(selected.getFirstName());
                    lastNameField.setText(selected.getLastName());
                    emailField.setText(selected.getEmail());
                    dobField.setText(selected.getDateOfBirth());
                    genderBox.setValue(selected.getGender());
                    phoneField.setText(selected.getPhone());

                    System.out.println("📌 Vybrán student s ID = " + selectedId);
                }
            });
            return row;
        });

        loadStudents();
    }

    @FXML
    private void onSearch() {
        String term = searchField.getText().toLowerCase();
        ObservableList<StudentDto> filtered = FXCollections.observableArrayList();
        for (StudentDto s : studentData) {
            if (s.getFirstName().toLowerCase().contains(term) || s.getLastName().toLowerCase().contains(term)) {
                filtered.add(s);
            }
        }
        studentTable.setItems(filtered);
    }

    @FXML
    private void onRefresh() {
        loadStudents();
        studentTable.setItems(studentData);
        searchField.clear();
    }

    @FXML
    private void onAdd() {
        String json = collectJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/students"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> Platform.runLater(() -> {
                    System.out.println("✅ Student byl přidán");
                    clearForm();
                    loadStudents();
                }));
    }

    @FXML
    private void onUpdate() {
        if (selectedId == null) {
            System.out.println("⚠️ Není vybrán žádný student pro úpravu");
            return;
        }
        String json = collectJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/students/" + selectedId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> Platform.runLater(() -> {
                    System.out.println("✏️ Student byl upraven (ID = " + selectedId + ")");
                    clearForm();
                    loadStudents();
                }));
    }

    @FXML
    private void onDelete() {
        if (selectedId == null) {
            return;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/students/" + selectedId))
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> Platform.runLater(() -> {
                    clearForm();
                    loadStudents();
                }));
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    private String collectJson() {
        return String.format("""
                {
                    "firstName":"%s",
                    "lastName":"%s",
                    "email":"%s",
                    "dateOfBirth":"%s",
                    "gender":"%s",
                    "phone":"%s"
                }
                """,
                firstNameField.getText(),
                lastNameField.getText(),
                emailField.getText(),
                dobField.getText(),
                genderBox.getValue(),
                phoneField.getText()
        );
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        dobField.clear();
        genderBox.setValue(null);
        phoneField.clear();
        selectedId = null;
        studentTable.getSelectionModel().clearSelection();
    }

    private void loadStudents() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/students"))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> Platform.runLater(() -> {
                    studentData.clear();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode array = mapper.readTree(body);
                        for (JsonNode o : array) {
                            studentData.add(new StudentDto(
                                    o.get("id").asLong(),
                                    o.get("firstName").asText(),
                                    o.get("lastName").asText(),
                                    o.get("email").asText(),
                                    o.get("dateOfBirth").asText(),
                                    o.get("gender").asText(),
                                    o.get("phone").asText()
                            ));
                        }
                        System.out.println("📥 Načteno " + array.size() + " studentů");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
    }
}
