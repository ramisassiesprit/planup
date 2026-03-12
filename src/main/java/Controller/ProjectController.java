package Controller;

import Entite.Project;
import Service.ServiceProject;
import Service.ServiceSprint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectController {

    @FXML
    private PieChart typePieChart;

    @FXML
    private TableView<Project> projectTable;

    @FXML
    private TableColumn<Project, Integer> colId;

    @FXML
    private TableColumn<Project, String> colName;

    @FXML
    private TableColumn<Project, String> colType;

    @FXML
    private TableColumn<Project, Double> colProgress;

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
    private ServiceSprint serviceSprint = new ServiceSprint();
    private ObservableList<Project> projectList = FXCollections.observableArrayList();
    private String userRole;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("idProject"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Configuration de la barre de progression
        colProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));
        colProgress.setCellFactory(column -> new TableCell<Project, Double>() {
            private final ProgressBar progressBar = new ProgressBar();

            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    progressBar.setProgress(item);
                    progressBar.setMaxWidth(Double.MAX_VALUE);

                    // Style de la barre selon l'avancement
                    if (item >= 1.0) {
                        progressBar.setStyle("-fx-accent: #2ecc71;"); // Vert
                    } else if (item > 0.5) {
                        progressBar.setStyle("-fx-accent: #3498db;"); // Bleu
                    } else {
                        progressBar.setStyle("-fx-accent: #f1c40f;"); // Jaune
                    }

                    setGraphic(progressBar);
                }
            }
        });

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
            List<Project> projects = serviceProject.readAll();
            for (Project p : projects) {
                p.setProgress(serviceSprint.getProjectProgress(p.getIdProject()));
            }
            projectList.addAll(projects);
            projectTable.setItems(projectList);
            updateTypeChart(projects);
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
        confirmation.setContentText(
                "Êtes-vous sûr de vouloir supprimer ce projet ?\nCette action supprimera également tous les sprints associés.");

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

    private void updateTypeChart(List<Project> projects) {
        Map<String, Long> typeCounts = projects.stream()
                .collect(Collectors.groupingBy(Project::getType, Collectors.counting()));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        typeCounts.forEach((type, count) -> {
            pieData.add(new PieChart.Data(type + " (" + count + ")", count));
        });

        typePieChart.setData(pieData);

        for (PieChart.Data data : pieData) {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue());
            Tooltip.install(data.getNode(), tooltip);
            data.getNode().setOnMouseEntered(e -> data.getNode().setStyle("-fx-opacity: 0.8; -fx-cursor: hand;"));
            data.getNode().setOnMouseExited(e -> data.getNode().setStyle("-fx-opacity: 1.0;"));
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
