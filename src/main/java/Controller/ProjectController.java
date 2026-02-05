package Controller;

import Entite.Project;
import Service.ServiceProject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.Optional;

public class ProjectController {

    @FXML
    private TableView<Project> projectTable;

    @FXML
    private TableColumn<Project, Integer> colId;

    @FXML
    private TableColumn<Project, String> colName;

    @FXML
    private TableColumn<Project, String> colType;

    @FXML
    private TextField nameField;

    @FXML
    private TextField typeField;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;

    @FXML
    private VBox formContainer;

    private ServiceProject serviceProject = new ServiceProject();
    private ObservableList<Project> projectList = FXCollections.observableArrayList();
    private String userRole;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("idProject"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Charger les données
        loadProjects();

        // Sélection dans le tableau
        projectTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                typeField.setText(newSelection.getType());
            }
        });
    }

    /**
     * Configure les permissions selon le rôle
     */
    public void setUserRole(String role) {
        this.userRole = role;
        
        // Seuls ADMIN et MANAGER peuvent modifier
        boolean canEdit = "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role);
        
        // Si ce n'est pas un manager ou admin, on cache le formulaire de saisie
        if (formContainer != null) {
            formContainer.setVisible(canEdit);
            formContainer.setManaged(canEdit);
        }
    }

    private void loadProjects() {
        try {
            projectList.clear();
            projectList.addAll(serviceProject.readAll());
            projectTable.setItems(projectList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les projets", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();

        if (name.isEmpty() || type.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Champs requis", 
                    "Veuillez remplir tous les champs");
            return;
        }

        try {
            Project project = new Project(name, type);
            if (serviceProject.ajouter(project)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet ajouté", 
                        "Le projet a été ajouté avec succès");
                clearFields();
                loadProjects();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le projet", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun projet sélectionné", 
                    "Veuillez sélectionner un projet à modifier");
            return;
        }

        String name = nameField.getText().trim();
        String type = typeField.getText().trim();

        if (name.isEmpty() || type.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Champs requis", 
                    "Veuillez remplir tous les champs");
            return;
        }

        try {
            selected.setName(name);
            selected.setType(type);
            if (serviceProject.modifier(selected)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet modifié", 
                        "Le projet a été modifié avec succès");
                clearFields();
                loadProjects();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier le projet", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun projet sélectionné", 
                    "Veuillez sélectionner un projet à supprimer");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le projet");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce projet ?\nCette action supprimera également tous les sprints associés.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (serviceProject.supprimer(selected)) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet supprimé", 
                            "Le projet a été supprimé avec succès");
                    clearFields();
                    loadProjects();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le projet", e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        projectTable.getSelectionModel().clearSelection();
    }

    private void clearFields() {
        nameField.clear();
        typeField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
