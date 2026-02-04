package Controller;

import Entite.Project;
import Service.ServiceProject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class ProjectController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField typeField;
    @FXML
    private TableView<Project> projectTable;
    @FXML
    private TableColumn<Project, Integer> idColumn;
    @FXML
    private TableColumn<Project, String> nameColumn;
    @FXML
    private TableColumn<Project, String> typeColumn;

    private ServiceProject serviceProject = new ServiceProject();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idProject"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        loadProjects();
    }

    private void loadProjects() {
        try {
            List<Project> projects = serviceProject.readAll();
            ObservableList<Project> observableList = FXCollections.observableArrayList(projects);
            projectTable.setItems(observableList);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les projets: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProject() {
        String name = nameField.getText();
        String type = typeField.getText();

        if (name.isEmpty() || type.isEmpty()) {
            showAlert("Avertissement", "Veuillez remplir tous les champs.");
            return;
        }

        Project project = new Project(name, type);
        try {
            if (serviceProject.ajouter(project)) {
                loadProjects();
                clearFields();
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void clearFields() {
        nameField.clear();
        typeField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
