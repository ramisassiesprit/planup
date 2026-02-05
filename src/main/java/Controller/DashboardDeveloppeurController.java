package Controller;

import Entite.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class DashboardDeveloppeurController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label userInfoLabel;

    private String userRole = "DEVELOPPEUR";

    public void setUserInfo(Utilisateur user) {
        if (user != null) {
            userInfoLabel.setText("Dashboard DEVELOPPEUR - " + user.getPrenom() + " " + user.getNom());
            this.userRole = user.getRole();
        }
    }

    @FXML
    private void showProjects() {
        loadViewWithRole("/view/ProjectView.fxml", "DEVELOPPEUR");
    }

    @FXML
    private void showSprints() {
        loadViewWithRole("/view/SprintView.fxml", "DEVELOPPEUR");
    }

    @FXML
    private void showMyTasks() {
        // Placeholder pour les tâches
    }

    @FXML
    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadViewWithRole(String fxmlPath, String role) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Le fichier FXML n'a pas été trouvé : " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            // Passer le rôle au contrôleur
            Object controller = loader.getController();
            if (controller instanceof ProjectController) {
                ((ProjectController) controller).setUserRole(role);
            } else if (controller instanceof SprintController) {
                ((SprintController) controller).setUserRole(role);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
