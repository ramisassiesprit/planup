package Controller;

import Entite.Project;
import Entite.Sprint;
import Service.ServiceProject;
import Service.ServiceSprint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import java.sql.SQLException;
import java.util.List;

public class SprintController {

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<Project> projectComboBox;
    @FXML
    private TableView<Sprint> sprintTable;
    @FXML
    private TableColumn<Sprint, Integer> idColumn;
    @FXML
    private TableColumn<Sprint, String> nameColumn;
    @FXML
    private TableColumn<Sprint, String> projectColumn;

    private ServiceSprint serviceSprint = new ServiceSprint();
    private ServiceProject serviceProject = new ServiceProject();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idSprint"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Affichage du nom du projet au lieu de l'objet complet
        projectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getProject() != null ? cellData.getValue().getProject().getName() : "N/A"));

        loadProjectsIntoComboBox();
        loadSprints();
    }

    private void loadProjectsIntoComboBox() {
        try {
            List<Project> projects = serviceProject.readAll();
            projectComboBox.setItems(FXCollections.observableArrayList(projects));

            // Personnaliser l'affichage dans la ComboBox
            projectComboBox.setCellFactory(param -> new ListCell<Project>() {
                @Override
                protected void updateItem(Project item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            projectComboBox.setButtonCell(new ListCell<Project>() {
                @Override
                protected void updateItem(Project item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les projets: " + e.getMessage());
        }
    }

    private void loadSprints() {
        try {
            List<Sprint> sprints = serviceSprint.readAll();
            sprintTable.setItems(FXCollections.observableArrayList(sprints));
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les sprints: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddSprint() {
        String name = nameField.getText();
        Project selectedProject = projectComboBox.getSelectionModel().getSelectedItem();

        if (name.isEmpty() || selectedProject == null) {
            showAlert("Avertissement", "Veuillez remplir tous les champs et sélectionner un projet.");
            return;
        }

        Sprint sprint = new Sprint(name, selectedProject);
        try {
            if (serviceSprint.ajouter(sprint)) {
                loadSprints();
                clearFields();
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void clearFields() {
        nameField.clear();
        projectComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
