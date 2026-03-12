package Controller;

import Entite.*;
import Service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsController {

    @FXML private PieChart userRolePieChart;
    @FXML private BarChart<String, Number> taskStatusBarChart;
    @FXML private PieChart congeStatusPieChart;
    @FXML private BarChart<String, Number> projectProgressBarChart;
    @FXML private CategoryAxis taskCategoryAxis;
    @FXML private NumberAxis taskNumberAxis;
    @FXML private CategoryAxis projectCategoryAxis;
    @FXML private NumberAxis projectNumberAxis;

    // Stat card labels
    @FXML private Label totalUsersLabel;
    @FXML private Label totalProjectsLabel;
    @FXML private Label totalTasksLabel;
    @FXML private Label totalCongesLabel;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private final ServiceTache serviceTache = new ServiceTache();
    private final ServiceProject serviceProject = new ServiceProject();
    private final ServiceSprint serviceSprint = new ServiceSprint();
    private final CongeService congeService = new CongeService();

    private Utilisateur currentUser;

    @FXML
    public void initialize() {
        // Initial setup if needed
    }

    public void setLoggedInUser(Utilisateur user) {
        this.currentUser = user;
        refreshData();
    }

    private void refreshData() {
        loadUserRoleChart();
        loadTaskStatusChart();
        loadCongeStatusChart();
        loadProjectProgressChart();
        loadStatCards();
    }

    private boolean isRHOrAdmin() {
        return currentUser != null && (currentUser.getRole().equalsIgnoreCase("RH") || currentUser.getRole().equalsIgnoreCase("ADMIN"));
    }

    private void loadStatCards() {
        try {
            List<Utilisateur> users = serviceUtilisateur.afficher();
            if (!isRHOrAdmin()) {
                users = users.stream().filter(u -> u.getCin() == currentUser.getCin()).collect(Collectors.toList());
            }
            totalUsersLabel.setText(String.valueOf(users.size()));

            List<Project> projects = serviceProject.readAll();
            if (!isRHOrAdmin()) {
                // Filter projects where user has tasks
                List<Tache> userTasks = serviceTache.readAll().stream()
                        .filter(t -> t.getAffecte() != null && t.getAffecte().getCin() == currentUser.getCin())
                        .collect(Collectors.toList());
                Set<Integer> projectIds = userTasks.stream()
                        .map(t -> (t.getSprint() != null && t.getSprint().getProject() != null) ? t.getSprint().getProject().getIdProject() : -1)
                        .collect(Collectors.toSet());
                projects = projects.stream().filter(p -> projectIds.contains(p.getIdProject())).collect(Collectors.toList());
            }
            totalProjectsLabel.setText(String.valueOf(projects.size()));

            List<Tache> tasks = serviceTache.readAll();
            if (!isRHOrAdmin()) {
                tasks = tasks.stream().filter(t -> t.getAffecte() != null && t.getAffecte().getCin() == currentUser.getCin()).collect(Collectors.toList());
            }
            totalTasksLabel.setText(String.valueOf(tasks.size()));

            List<Conge> conges = congeService.listerConges();
            if (!isRHOrAdmin()) {
                conges = conges.stream().filter(c -> c.getUtilisateur() != null && c.getUtilisateur().getCin() == currentUser.getCin()).collect(Collectors.toList());
            }
            totalCongesLabel.setText(String.valueOf(conges.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserRoleChart() {
        try {
            List<Utilisateur> users = serviceUtilisateur.afficher();
            if (!isRHOrAdmin()) {
                users = users.stream().filter(u -> u.getCin() == currentUser.getCin()).collect(Collectors.toList());
            }
            
            Map<String, Long> roleCount = users.stream()
                    .collect(Collectors.groupingBy(Utilisateur::getRole, Collectors.counting()));

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            roleCount.forEach((role, count) ->
                    pieData.add(new PieChart.Data(role + " (" + count + ")", count)));

            userRolePieChart.setData(pieData);
            userRolePieChart.setTitle(isRHOrAdmin() ? "Utilisateurs par Rôle" : "Votre Profil");
            userRolePieChart.setLabelsVisible(true);
            userRolePieChart.setLegendVisible(true);
            userRolePieChart.setAnimated(true);

            // Add tooltips
            for (PieChart.Data data : pieData) {
                if (data.getNode() != null) {
                    Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue());
                    Tooltip.install(data.getNode(), tooltip);
                    data.getNode().setOnMouseEntered(e -> data.getNode().setStyle("-fx-opacity: 0.8;"));
                    data.getNode().setOnMouseExited(e -> data.getNode().setStyle("-fx-opacity: 1.0;"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTaskStatusChart() {
        try {
            List<Tache> tasks = serviceTache.readAll();
            if (!isRHOrAdmin()) {
                tasks = tasks.stream().filter(t -> t.getAffecte() != null && t.getAffecte().getCin() == currentUser.getCin()).collect(Collectors.toList());
            }

            long pasEncoreFaite = tasks.stream().filter(t -> "PAS_ENCORE_FAITE".equalsIgnoreCase(t.getStatut())).count();
            long enCours = tasks.stream().filter(t -> "EN_COURS".equalsIgnoreCase(t.getStatut())).count();
            long terminee = tasks.stream().filter(t -> "TERMINEE".equalsIgnoreCase(t.getStatut()) || "DEJA_FAITE".equalsIgnoreCase(t.getStatut())).count();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Nombre de tâches");
            series.getData().add(new XYChart.Data<>("À faire", pasEncoreFaite));
            series.getData().add(new XYChart.Data<>("En cours", enCours));
            series.getData().add(new XYChart.Data<>("Terminées", terminee));

            taskStatusBarChart.getData().clear();
            taskStatusBarChart.getData().add(series);
            taskStatusBarChart.setTitle(isRHOrAdmin() ? "Répartition Globale des Tâches" : "Mes Tâches");
            taskStatusBarChart.setAnimated(true);
            taskStatusBarChart.setLegendVisible(false);

            // Color the bars after rendering
            javafx.application.Platform.runLater(() -> {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        String color;
                        switch (data.getXValue()) {
                            case "À faire": color = "#94a3b8"; break;
                            case "En cours": color = "#3b82f6"; break;
                            case "Terminées": color = "#10b981"; break;
                            default: color = "#6366f1";
                        }
                        data.getNode().setStyle("-fx-bar-fill: " + color + ";");

                        Tooltip tooltip = new Tooltip(data.getXValue() + ": " + data.getYValue());
                        Tooltip.install(data.getNode(), tooltip);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCongeStatusChart() {
        try {
            List<Conge> conges = congeService.listerConges();
            if (!isRHOrAdmin()) {
                conges = conges.stream().filter(c -> c.getUtilisateur() != null && c.getUtilisateur().getCin() == currentUser.getCin()).collect(Collectors.toList());
            }
            
            Map<String, Long> statusCount = conges.stream()
                    .collect(Collectors.groupingBy(Conge::getStatut, Collectors.counting()));

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            statusCount.forEach((statut, count) -> {
                String label;
                switch (statut) {
                    case "EN_ATTENTE": label = "En attente"; break;
                    case "ACCEPTE": label = "Accepté"; break;
                    case "REFUSE": label = "Refusé"; break;
                    case "ANNULE": label = "Annulé"; break;
                    default: label = statut;
                }
                pieData.add(new PieChart.Data(label + " (" + count + ")", count));
            });

            congeStatusPieChart.setData(pieData);
            congeStatusPieChart.setTitle(isRHOrAdmin() ? "Congés Globaux par Statut" : "Mes Congés");
            congeStatusPieChart.setLabelsVisible(true);
            congeStatusPieChart.setLegendVisible(true);
            congeStatusPieChart.setAnimated(true);

            // Add tooltips
            for (PieChart.Data data : pieData) {
                if (data.getNode() != null) {
                    Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue());
                    Tooltip.install(data.getNode(), tooltip);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProjectProgressChart() {
        try {
            List<Project> projects = serviceProject.readAll();
            if (!isRHOrAdmin()) {
                List<Tache> userTasks = serviceTache.readAll().stream()
                        .filter(t -> t.getAffecte() != null && t.getAffecte().getCin() == currentUser.getCin())
                        .collect(Collectors.toList());
                Set<Integer> projectIds = userTasks.stream()
                        .map(t -> (t.getSprint() != null && t.getSprint().getProject() != null) ? t.getSprint().getProject().getIdProject() : -1)
                        .collect(Collectors.toSet());
                projects = projects.stream().filter(p -> projectIds.contains(p.getIdProject())).collect(Collectors.toList());
            }

            XYChart.Series<String, Number> completedSeries = new XYChart.Series<>();
            completedSeries.setName("Progression (%)");

            for (Project p : projects) {
                double progress = serviceSprint.getProjectProgress(p.getIdProject());
                p.setProgress(progress);
                completedSeries.getData().add(new XYChart.Data<>(p.getName(), Math.round(progress * 100)));
            }

            projectProgressBarChart.getData().clear();
            projectProgressBarChart.getData().add(completedSeries);
            projectProgressBarChart.setTitle(isRHOrAdmin() ? "Progression de tous les Projets" : "Mes Projets impliqués");
            projectProgressBarChart.setAnimated(true);
            projectProgressBarChart.setLegendVisible(false);

            // Color the bars based on progress
            javafx.application.Platform.runLater(() -> {
                for (XYChart.Data<String, Number> data : completedSeries.getData()) {
                    if (data.getNode() != null) {
                        int val = data.getYValue().intValue();
                        String color;
                        if (val >= 80) color = "#10b981";
                        else if (val >= 50) color = "#3b82f6";
                        else if (val >= 25) color = "#f59e0b";
                        else color = "#ef4444";
                        data.getNode().setStyle("-fx-bar-fill: " + color + ";");

                        Tooltip tooltip = new Tooltip(data.getXValue() + ": " + val + "%");
                        Tooltip.install(data.getNode(), tooltip);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
