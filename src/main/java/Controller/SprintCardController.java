package Controller;

import Entite.Sprint;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;


public class SprintCardController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label projectLabel;

    @FXML
    private Label progressText;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private HBox actionsBox;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    private Sprint sprint;
    private SprintController mainController;

    public void setData(Sprint sprint, SprintController mainController, String userRole) {
        this.sprint = sprint;
        this.mainController = mainController;

        nameLabel.setText(sprint.getName());
        projectLabel.setText(sprint.getProject() != null ? sprint.getProject().getName() : "Aucun projet");
        
        double progress = sprint.getProgress();
        progressBar.setProgress(progress);
        progressText.setText((int)(progress * 100) + "%");

        // Style de la barre
        if (progress >= 1.0) {
            progressBar.setStyle("-fx-accent: #10b981;");
        } else if (progress > 0.5) {
            progressBar.setStyle("-fx-accent: #6366f1;");
        } else {
            progressBar.setStyle("-fx-accent: #f59e0b;");
        }

        // Permissions
        boolean canEdit = "ADMIN".equalsIgnoreCase(userRole) || "MANAGER".equalsIgnoreCase(userRole);
        actionsBox.setVisible(canEdit);
        actionsBox.setManaged(canEdit);
    }

    @FXML
    private void handleCardClick() {
        if (mainController != null) {
            mainController.selectSprint(sprint);
        }
    }

    @FXML
    private void handleEdit() {
        if (mainController != null) {
            mainController.selectSprintAndFocus(sprint);
        }
    }

    @FXML
    private void handleDelete() {
        if (mainController != null) {
            mainController.deleteSprint(sprint);
        }
    }
}
