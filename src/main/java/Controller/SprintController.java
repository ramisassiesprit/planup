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

import java.sql.SQLException;
import java.util.Optional;

public class SprintController {

    @FXML
    private TableView<Sprint> sprintTable;

    @FXML
    private TableColumn<Sprint, Integer> colId;

    @FXML
    private TableColumn<Sprint, String> colName;

    @FXML
    private TableColumn<Sprint, String> colProject;

    @FXML
    private Label formTitle;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<Project> projectComboBox;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnFilter;

    private ServiceSprint serviceSprint = new ServiceSprint();
    private ServiceProject serviceProject = new ServiceProject();
    private ObservableList<Sprint> sprintList = FXCollections.observableArrayList();
    private ObservableList<Project> projectList = FXCollections.observableArrayList();
    private String userRole;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("idSprint"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProject.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getProject() != null ? cellData.getValue().getProject().getName() : "N/A"));

        // Charger les projets dans le ComboBox
        loadProjects();

        // Charger les sprints
        loadSprints();

        // Sélection dans le tableau
        sprintTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && ("ADMIN".equalsIgnoreCase(userRole) || "MANAGER".equalsIgnoreCase(userRole))) {
                nameField.setText(newSelection.getName());
                projectComboBox.setValue(newSelection.getProject());
            }
        });

        // Configuration du ComboBox pour afficher le nom du projet
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
    }

    /**
     * Configure les permissions selon le rôle
     */
    public void setUserRole(String role) {
        this.userRole = role;

        // Seuls ADMIN et MANAGER peuvent modifier
        boolean canEdit = "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role);

        // Cacher les boutons de modification
        btnAdd.setVisible(canEdit);
        btnUpdate.setVisible(canEdit);
        btnDelete.setVisible(canEdit);

        btnAdd.setManaged(canEdit);
        btnUpdate.setManaged(canEdit);
        btnDelete.setManaged(canEdit);

        // Cacher les champs de saisie pour le développeur mais garder le ComboBox de
        // projet pour le filtre
        if (!canEdit) {
            if (formTitle != null) {
                formTitle.setText("Filtrage des Sprints");
            }
            if (nameLabel != null) {
                nameLabel.setVisible(false);
                nameLabel.setManaged(false);
            }
            if (nameField != null) {
                nameField.setVisible(false);
                nameField.setManaged(false);
            }
        }

        // S'assurer que le bouton de filtre et le bouton effacer sont toujours visibles
        btnFilter.setVisible(true);
        btnFilter.setManaged(true);
    }

    private void loadProjects() {
        try {
            projectList.clear();
            projectList.addAll(serviceProject.readAll());
            projectComboBox.setItems(projectList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les projets", e.getMessage());
        }
    }

    private void loadSprints() {
        try {
            sprintList.clear();
            sprintList.addAll(serviceSprint.readAll());
            sprintTable.setItems(sprintList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les sprints", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        Project selectedProject = projectComboBox.getValue();

        if (name.isEmpty() || selectedProject == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Champs requis",
                    "Veuillez remplir tous les champs et sélectionner un projet");
            return;
        }

        try {
            Sprint sprint = new Sprint(name, selectedProject);
            if (serviceSprint.ajouter(sprint)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Sprint ajouté",
                        "Le sprint a été ajouté avec succès");
                clearFields();
                loadSprints();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le sprint", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Sprint selected = sprintTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun sprint sélectionné",
                    "Veuillez sélectionner un sprint à modifier");
            return;
        }

        String name = nameField.getText().trim();
        Project selectedProject = projectComboBox.getValue();

        if (name.isEmpty() || selectedProject == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Champs requis",
                    "Veuillez remplir tous les champs et sélectionner un projet");
            return;
        }

        try {
            selected.setName(name);
            selected.setProject(selectedProject);
            if (serviceSprint.modifier(selected)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Sprint modifié",
                        "Le sprint a été modifié avec succès");
                clearFields();
                loadSprints();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier le sprint", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Sprint selected = sprintTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection", "Aucun sprint sélectionné",
                    "Veuillez sélectionner un sprint à supprimer");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le sprint");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce sprint ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (serviceSprint.supprimer(selected)) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Sprint supprimé",
                            "Le sprint a été supprimé avec succès");
                    clearFields();
                    loadSprints();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le sprint", e.getMessage());
            }
        }
    }

    @FXML
    private void handleFilter() {
        Project selectedProject = projectComboBox.getValue();
        if (selectedProject == null) {
            loadSprints(); // Afficher tous les sprints
            return;
        }

        try {
            sprintList.clear();
            sprintList.addAll(serviceSprint.getSprintsByProject(selectedProject.getIdProject()));
            sprintTable.setItems(sprintList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de filtrer les sprints", e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        sprintTable.getSelectionModel().clearSelection();
        loadSprints(); // Réafficher tous les sprints
    }

    private void clearFields() {
        nameField.clear();
        projectComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
