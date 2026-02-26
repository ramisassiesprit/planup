package Controller;

import Entite.Utilisateur;
import Service.ServiceAuthentification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final ServiceAuthentification authService = new ServiceAuthentification();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String motDePasse = passwordField.getText();

        Utilisateur user = authService.login(email, motDePasse);
        if (user != null) {
            String role = user.getRole();
            String fxmlFile = "";

            switch (role.toUpperCase()) {
                case "ADMIN":
                    fxmlFile = "/view/DashboardAdmin.fxml";
                    break;
                case "MANAGER":
                    fxmlFile = "/view/DashboardManager.fxml";
                    break;
                case "RH":
                    fxmlFile = "/view/DashboardRH.fxml";
                    break;
                case "DEVELOPPEUR":
                    fxmlFile = "/view/DashboardDeveloppeur.fxml";
                    break;
                case "INTEGRATEUR":
                    fxmlFile = "/view/DashboardIntegrateur.fxml";
                    break;
                case "CANDIDAT":
                    fxmlFile = "/view/DashboardCandidat.fxml";
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Role inconnu", "Votre rôle n'est pas reconnu.");
                    return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();

                // Passer les données à l'un des contrôleurs (tous ont setUserInfo)
                if (role.equalsIgnoreCase("ADMIN")) {
                    DashboardAdminController ctrl = loader.getController();
                    ctrl.setUserInfo(user);
                } else if (role.equalsIgnoreCase("MANAGER")) {
                    DashboardManagerController ctrl = loader.getController();
                    ctrl.setUserInfo(user);
                } else if (role.equalsIgnoreCase("RH")) {
                    DashboardRHController ctrl = loader.getController();
                    ctrl.setUserInfo(user);
                } else if (role.equalsIgnoreCase("DEVELOPPEUR")) {
                    DashboardDeveloppeurController ctrl = loader.getController();
                    ctrl.setUserInfo(user);
                } else if (role.equalsIgnoreCase("INTEGRATEUR")) {
                    DashboardIntegrateurController ctrl = loader.getController();
                    ctrl.setUserInfo(user);
                } else if (role.equalsIgnoreCase("CANDIDAT")) {
                    DashboardCandidatController ctrl = loader.getController();
                    ctrl.setUserInfo(user);
                }

                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setTitle("PlanUp - Dashboard " + role);
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Chargement échoué",
                        "Impossible de charger le tableau de bord.");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Authentification", "Connexion échouée",
                    "Email ou mot de passe incorrect");
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
