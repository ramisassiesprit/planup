package Controller;

import Entite.Tache;
import Entite.Sprint;
import Entite.Utilisateur;
import Entite.Project;
import Service.ServiceTache;
import Service.ServiceUtilisateur;
import Service.ServiceSprint;
import Service.ServiceProject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.scene.chart.PieChart; // Added import for PieChart

import java.sql.SQLException;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TaskController {

    @FXML
    private PieChart taskDistributionChart; // Added PieChart field

    @FXML
    private ListView<Tache> pasEncoreFaiteList;
    @FXML
    private ListView<Tache> enCoursList;
    @FXML
    private ListView<Tache> termineeList;

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
    private ComboBox<Project> projectCombo;
    @FXML
    private ComboBox<Sprint> sprintCombo;
    @FXML
    private ComboBox<Utilisateur> assigneeCombo;

    @FXML
    private VBox formContainer;
    @FXML
    private ScrollPane formScrollPane;
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
    private ServiceProject serviceProject = new ServiceProject();

    private ObservableList<Tache> taskList = FXCollections.observableArrayList();
    private String userRole;
    private Utilisateur currentUser;

    @FXML
    public void initialize() {
        setupComboBoxConverters();
        setupProjectSprintSync();

        setupListView(pasEncoreFaiteList, "PAS_ENCORE_FAITE");
        setupListView(enCoursList, "EN_COURS");
        setupListView(termineeList, "TERMINEE");

        loadData();
    }

    public void setRoleAndUser(String role, Utilisateur user) {
        this.userRole = role;
        this.currentUser = user;

        boolean canCrud = "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role);

        if (formScrollPane != null) {
            formScrollPane.setVisible(canCrud);
            formScrollPane.setManaged(canCrud);
        } else if (formContainer != null) {
            formContainer.setVisible(canCrud);
            formContainer.setManaged(canCrud);
        }

        if (btnAssign != null) {
            btnAssign.setVisible("MANAGER".equalsIgnoreCase(role));
        }

        loadData();
    }

    private void setupListView(ListView<Tache> listView, String targetStatus) {
        listView.setCellFactory(lv -> new TaskListCell());

        listView.setOnDragOver(event -> {
            if (event.getGestureSource() != listView && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        listView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int taskId = Integer.parseInt(db.getString());
                try {
                    if (serviceTache.updateStatus(taskId, targetStatus)) {
                        success = true;
                        loadData();
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Mise à jour échouée", e.getMessage());
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (listView != pasEncoreFaiteList)
                    pasEncoreFaiteList.getSelectionModel().clearSelection();
                if (listView != enCoursList)
                    enCoursList.getSelectionModel().clearSelection();
                if (listView != termineeList)
                    termineeList.getSelectionModel().clearSelection();
                populateForm(newVal);
            }
        });
    }

    private class TaskListCell extends ListCell<Tache> {
        private VBox content;
        private Label nameLabel;
        private Label assigneeLabel;
        private Label timerLabel;
        private Timeline timeline;
        private int remainingSeconds;

        public TaskListCell() {
            super();
            nameLabel = new Label();
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            assigneeLabel = new Label();
            assigneeLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

            timerLabel = new Label();

            content = new VBox(5, nameLabel, assigneeLabel, timerLabel);
            content.setPadding(new Insets(10));
            content.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");

            // Drag functionality
            setOnDragDetected(event -> {
                if (getItem() == null)
                    return;
                boolean canEdit = "DEVELOPPEUR".equalsIgnoreCase(userRole)
                        || "INTEGRATEUR".equalsIgnoreCase(userRole)
                        || "MANAGER".equalsIgnoreCase(userRole)
                        || "ADMIN".equalsIgnoreCase(userRole);
                if (!canEdit)
                    return;

                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(String.valueOf(getItem().getIdTache()));
                db.setContent(cc);
                event.consume();
            });
        }

        @Override
        protected void updateItem(Tache item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                if (timeline != null) {
                    timeline.stop();
                    timeline = null;
                }
            } else {
                nameLabel.setText(item.getName());
                String assignee = item.getAffecte() != null ? item.getAffecte().getNom() : "Non assigné";
                assigneeLabel.setText("👤 " + assignee + " | Priorité: " + item.getPriorite());

                if ("EN_COURS".equals(item.getStatut())) {
                    timerLabel.setVisible(true);
                    timerLabel.setManaged(true);

                    if (timeline != null)
                        timeline.stop();

                    // L'estimation est en heures
                    remainingSeconds = item.getEstimation() * 3600;

                    timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                        remainingSeconds--;
                        updateTimerLabel();
                    }));
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play();
                    updateTimerLabel();
                } else {
                    timerLabel.setVisible(false);
                    timerLabel.setManaged(false);
                    if (timeline != null) {
                        timeline.stop();
                        timeline = null;
                    }
                }

                setGraphic(content);
                // Ensure correct background (transparent container for cell)
                setStyle("-fx-background-color: transparent; -fx-padding: 4;");
            }
        }

        private void updateTimerLabel() {
            int absSeconds = Math.abs(remainingSeconds);
            int h = absSeconds / 3600;
            int m = (absSeconds % 3600) / 60;
            int s = absSeconds % 60;
            String timeStr = String.format("%02d:%02d:%02d", h, m, s);

            if (remainingSeconds >= 0) {
                timerLabel.setText("⏳ " + timeStr);
                timerLabel.setStyle(
                        "-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11px;");
            } else {
                timerLabel.setText("⏳ - " + timeStr);
                timerLabel.setStyle(
                        "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11px;");
            }
        }
    }

    private void setupComboBoxConverters() {
        sprintCombo.setConverter(new StringConverter<Sprint>() {
            @Override
            public String toString(Sprint sprint) {
                if (sprint == null)
                    return "";
                return sprint.getName() + " ("
                        + (sprint.getProject() != null ? sprint.getProject().getName() : "Aucun projet") + ")";
            }

            @Override
            public Sprint fromString(String string) {
                return null;
            }
        });

        assigneeCombo.setConverter(new StringConverter<Utilisateur>() {
            @Override
            public String toString(Utilisateur user) {
                if (user == null)
                    return "";
                return user.getNom() + " " + user.getPrenom() + " (" + user.getRole() + ")";
            }

            @Override
            public Utilisateur fromString(String string) {
                return null;
            }
        });

        projectCombo.setConverter(new StringConverter<Project>() {
            @Override
            public String toString(Project project) {
                if (project == null)
                    return "";
                return project.getName() + " (" + project.getType() + ")";
            }

            @Override
            public Project fromString(String string) {
                return null;
            }
        });
    }

    private void setupProjectSprintSync() {
        sprintCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getProject() != null) {
                if (projectCombo.getValue() == null
                        || projectCombo.getValue().getIdProject() != newVal.getProject().getIdProject()) {
                    projectCombo.setValue(newVal.getProject());
                }
            }
        });

        projectCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal != null) {
                    List<Sprint> sprintsFiltered = serviceSprint.getSprintsByProject(newVal.getIdProject());
                    sprintCombo.setItems(FXCollections.observableArrayList(sprintsFiltered));
                } else {
                    sprintCombo.setItems(FXCollections.observableArrayList(serviceSprint.readAll()));
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors du filtrage des sprints : " + e.getMessage());
            }
        });
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

            pasEncoreFaiteList.setItems(FXCollections.observableArrayList(
                    taskList.stream().filter(t -> "PAS_ENCORE_FAITE".equalsIgnoreCase(t.getStatut()))
                            .collect(Collectors.toList())));

            enCoursList.setItems(FXCollections.observableArrayList(
                    taskList.stream().filter(t -> "EN_COURS".equalsIgnoreCase(t.getStatut()))
                            .collect(Collectors.toList())));

            termineeList.setItems(FXCollections.observableArrayList(
                    taskList.stream().filter(t -> "TERMINEE".equalsIgnoreCase(t.getStatut())
                            || "DEJA_FAITE".equalsIgnoreCase(t.getStatut())).collect(Collectors.toList())));

            if ("MANAGER".equalsIgnoreCase(userRole) || "ADMIN".equalsIgnoreCase(userRole)) {
                List<Utilisateur> users = serviceUtilisateur.readAll().stream()
                        .filter(u -> "DEVELOPPEUR".equalsIgnoreCase(u.getRole())
                                || "INTEGRATEUR".equalsIgnoreCase(u.getRole()))
                        .collect(Collectors.toList());
                assigneeCombo.setItems(FXCollections.observableArrayList(users));

                projectCombo.setItems(FXCollections.observableArrayList(serviceProject.readAll()));
                sprintCombo.setItems(FXCollections.observableArrayList(serviceSprint.readAll()));
            }
            updateChart();
        } catch (SQLException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }

    private Tache getSelectedTask() {
        Tache t = pasEncoreFaiteList.getSelectionModel().getSelectedItem();
        if (t != null)
            return t;
        t = enCoursList.getSelectionModel().getSelectedItem();
        if (t != null)
            return t;
        t = termineeList.getSelectionModel().getSelectedItem();
        return t;
    }

    private void populateForm(Tache t) {
        nameField.setText(t.getName());
        descArea.setText(t.getDescription());
        if (t.getDateLimite() != null)
            deadlinePicker.setValue(t.getDateLimite().toLocalDate());
        priorityField.setText(String.valueOf(t.getPriorite()));
        estimationField.setText(String.valueOf(t.getEstimation()));

        if (t.getSprint() != null) {
            for (Sprint s : sprintCombo.getItems()) {
                if (s.getIdSprint() == t.getSprint().getIdSprint()) {
                    sprintCombo.setValue(s);
                    break;
                }
            }
        }

        if (t.getSprint() != null && t.getSprint().getProject() != null) {
            for (Project p : projectCombo.getItems()) {
                if (p.getIdProject() == t.getSprint().getProject().getIdProject()) {
                    projectCombo.setValue(p);
                    break;
                }
            }
        }

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
        Tache selected = getSelectedTask();
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
        Tache selected = getSelectedTask();
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
    private void handleAssign() {
        Tache selected = getSelectedTask();
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
        projectCombo.setValue(null);
        sprintCombo.setValue(null);
        assigneeCombo.setValue(null);
        pasEncoreFaiteList.getSelectionModel().clearSelection();
        enCoursList.getSelectionModel().clearSelection();
        termineeList.getSelectionModel().clearSelection();
    }

    private void updateChart() {
        long pasEncore = taskList.stream().filter(t -> "PAS_ENCORE_FAITE".equalsIgnoreCase(t.getStatut())).count();
        long enCours = taskList.stream().filter(t -> "EN_COURS".equalsIgnoreCase(t.getStatut())).count();
        long terminee = taskList.stream().filter(t -> "TERMINEE".equalsIgnoreCase(t.getStatut()) || "DEJA_FAITE".equalsIgnoreCase(t.getStatut())).count();

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("À faire (" + pasEncore + ")", pasEncore),
                new PieChart.Data("En cours (" + enCours + ")", enCours),
                new PieChart.Data("Terminées (" + terminee + ")", terminee)
        );

        taskDistributionChart.setData(pieData);

        for (PieChart.Data data : pieData) {
            Tooltip tooltip = new Tooltip(data.getName());
            Tooltip.install(data.getNode(), tooltip);
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
