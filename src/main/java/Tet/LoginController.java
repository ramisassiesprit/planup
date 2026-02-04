package Tet;

import Entite.Utilisateur;
import Service.ServiceAuthentification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
            showAlert(Alert.AlertType.INFORMATION, "Authentification", "Connexion réussie",
                    "Bienvenue " + user.getPrenom() + " " + user.getNom());
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
