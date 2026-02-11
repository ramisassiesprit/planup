package Controller;

import Entite.Tache;
import Entite.Sprint;
import Entite.Utilisateur;
import Service.ServiceTache;
import Service.ServiceUtilisateur;
import Service.ServiceSprint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TaskController {

    @FXML
    private TableView<Tache> taskTable;
    @FXML
    private TableColumn<Tache, Integer> colId;
    @FXML
    private TableColumn<Tache, String> colName;
    @FXML
    private TableColumn<Tache, String> colStatus;
    @FXML
    private TableColumn<Tache, String> colAssignee;

    @FXML
    private TextField nameField;
    @FXML
    private TextArea descArea;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private TextField priorityField;
    @FXML
    private TextField estimationField;
    @FXML
    private ComboBox<Sprint> sprintCombo;
    @FXML
    private ComboBox<Utilisateur> assigneeCombo;
    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private VBox formContainer;
    @FXML
    private VBox statusContainer;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAssign;

    private ServiceTache serviceTache = new ServiceTache();
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private ServiceSprint serviceSprint = new ServiceSprint();

    private ObservableList<Tache> taskList = FXCollections.observableArrayList();
    private String userRole;
    private Utilisateur currentUser;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idTache"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colAssignee.setCellValueFactory(cellData -> {
            Utilisateur u = cellData.getValue().getAffecte();
            return new javafx.beans.property.SimpleStringProperty(u != null ? u.getNom() : "Non assignée");
        });

        statusCombo.setItems(FXCollections.observableArrayList("PAS_ENCORE_FAITE", "EN_COURS", "DEJA_FAITE"));

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });

        loadData();
    }

    public void setRoleAndUser(String role, Utilisateur user) {
        this.userRole = role;
        this.currentUser = user;

        boolean canCrud = "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role);
        boolean isDevOrInt = "DEVELOPPEUR".equalsIgnoreCase(role) || "INTEGRATEUR".equalsIgnoreCase(role);

        if (formContainer != null) {
            formContainer.setVisible(canCrud);
            formContainer.setManaged(canCrud);
        }

        if (statusContainer != null) {
            statusContainer.setVisible(isDevOrInt);
            statusContainer.setManaged(isDevOrInt);
        }

        if (btnAssign != null) {
            btnAssign.setVisible("MANAGER".equalsIgnoreCase(role));
        }

        loadData();
    }

    private void loadData() {
        try {
            taskList.clear();
            if (currentUser != null
                    && ("DEVELOPPEUR".equalsIgnoreCase(userRole) || "INTEGRATEUR".equalsIgnoreCase(userRole))) {
                taskList.addAll(serviceTache.findByAssignee(currentUser.getCin()));
            } else {
                taskList.addAll(serviceTache.readAll());
            }
            taskTable.setItems(taskList);

            if ("MANAGER".equalsIgnoreCase(userRole) || "ADMIN".equalsIgnoreCase(userRole)) {
                List<Utilisateur> users = serviceUtilisateur.readAll().stream()
                        .filter(u -> "DEVELOPPEUR".equalsIgnoreCase(u.getRole())
                                || "INTEGRATEUR".equalsIgnoreCase(u.getRole()))
                        .collect(Collectors.toList());
                assigneeCombo.setItems(FXCollections.observableArrayList(users));

                sprintCombo.setItems(FXCollections.observableArrayList(serviceSprint.readAll()));
            }
        } catch (SQLException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }

    private void populateForm(Tache t) {
        nameField.setText(t.getName());
        descArea.setText(t.getDescription());
        if (t.getDateLimite() != null)
            deadlinePicker.setValue(t.getDateLimite().toLocalDate());
        priorityField.setText(String.valueOf(t.getPriorite()));
        estimationField.setText(String.valueOf(t.getEstimation()));
        statusCombo.setValue(t.getStatut());

        // Match Sprint in combo
        if (t.getSprint() != null) {
            for (Sprint s : sprintCombo.getItems()) {
                if (s.getIdSprint() == t.getSprint().getIdSprint()) {
                    sprintCombo.setValue(s);
                    break;
                }
            }
        }

        // Match Assignee in combo
        if (t.getAffecte() != null) {
            for (Utilisateur u : assigneeCombo.getItems()) {
                if (u.getCin() == t.getAffecte().getCin()) {
                    assigneeCombo.setValue(u);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleAdd() {
        try {
            Tache t = new Tache();
            t.setName(nameField.getText());
            t.setDescription(descArea.getText());
            if (deadlinePicker.getValue() != null)
                t.setDateLimite(Date.valueOf(deadlinePicker.getValue()));
            t.setPriorite(priorityField.getText().isEmpty() ? 0 : Integer.parseInt(priorityField.getText()));
            t.setEstimation(estimationField.getText().isEmpty() ? 0 : Integer.parseInt(estimationField.getText()));
            t.setStatut("PAS_ENCORE_FAITE");
            t.setSprint(sprintCombo.getValue());
            t.setAffecte(assigneeCombo.getValue());
            if (t.getAffecte() != null)
                t.setDateAffectation(new Date(System.currentTimeMillis()));

            if (serviceTache.ajouter(t)) {
                loadData();
                handleClear();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Tâche ajoutée", "La tâche a été créée.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout échoué", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        Tache selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        try {
            selected.setName(nameField.getText());
            selected.setDescription(descArea.getText());
            if (deadlinePicker.getValue() != null)
                selected.setDateLimite(Date.valueOf(deadlinePicker.getValue()));
            selected.setPriorite(Integer.parseInt(priorityField.getText()));
            selected.setEstimation(Integer.parseInt(estimationField.getText()));
            selected.setSprint(sprintCombo.getValue());
            selected.setAffecte(assigneeCombo.getValue());

            if (serviceTache.modifier(selected)) {
                loadData();
                handleClear();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Tâche modifiée", "La tâche a été mise à jour.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Modification échouée", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Tache selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        try {
            if (serviceTache.supprimer(selected)) {
                loadData();
                handleClear();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Tâche supprimée", "La tâche a été supprimée.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Suppression échouée", e.getMessage());
        }
    }

    @FXML
    private void handleStatusChange() {
        Tache selected = taskTable.getSelectionModel().getSelectedItem();
        String newStatus = statusCombo.getValue();
        if (selected == null || newStatus == null)
            return;

        try {
            if (serviceTache.updateStatus(selected.getIdTache(), newStatus)) {
                loadData();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Statut mis à jour", "Nouveau statut : " + newStatus);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Mise à jour échouée", e.getMessage());
        }
    }

    @FXML
    private void handleAssign() {
        Tache selected = taskTable.getSelectionModel().getSelectedItem();
        Utilisateur user = assigneeCombo.getValue();
        if (selected == null || user == null)
            return;

        try {
            if (serviceTache.assignToUser(selected.getIdTache(), user.getCin())) {
                loadData();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Tâche assignée", "Tâche assignée à " + user.getNom());
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Assignation échouée", e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        descArea.clear();
        deadlinePicker.setValue(null);
        priorityField.clear();
        estimationField.clear();
        statusCombo.setValue(null);
        sprintCombo.setValue(null);
        assigneeCombo.setValue(null);
        taskTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
