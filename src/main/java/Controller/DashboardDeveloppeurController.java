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

public class DashboardDeveloppeurController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label userInfoLabel;

    public void setUserInfo(Utilisateur user) {
        if (user != null) {
            userInfoLabel.setText("Dashboard DEVELOPPEUR - " + user.getPrenom() + " " + user.getNom());
        }
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
}
