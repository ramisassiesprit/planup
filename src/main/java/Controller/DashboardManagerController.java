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

public class DashboardManagerController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label userInfoLabel;

    private String userRole = "MANAGER";
    private Utilisateur loggedInUser;

    @FXML
    public void initialize() {
        showProjects();
    }

    public void setUserInfo(Utilisateur user) {
        if (user != null) {
            this.loggedInUser = user;
            userInfoLabel.setText("Dashboard MANAGER - " + user.getPrenom() + " " + user.getNom());
            this.userRole = user.getRole();
        }
    }

    @FXML
    private void showProjects() {
        loadViewWithRole("/view/ProjectView.fxml", "MANAGER");
    }

    @FXML
    private void showSprints() {
        loadViewWithRole("/view/SprintView.fxml", "MANAGER");
    }

    @FXML
    private void showTasks() {
        loadViewWithRole("/view/TaskView.fxml", "MANAGER");
    }

    @FXML
    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
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

            Object controller = loader.getController();
            if (controller instanceof ProjectController) {
                ((ProjectController) controller).setUserRole(role);
            } else if (controller instanceof SprintController) {
                ((SprintController) controller).setUserRole(role);
            } else if (controller instanceof TaskController) {
                ((TaskController) controller).setRoleAndUser(role, loggedInUser);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Le fichier FXML n'a pas été trouvé : " + fxmlPath);
                return;
            }
            Parent view = FXMLLoader.load(resource);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
