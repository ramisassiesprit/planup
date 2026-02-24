package Controller;

import Entite.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ProfileController {
    @FXML
    private Label nomLabel;
    @FXML
    private Label prenomLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label roleLabel;

    private Utilisateur utilisateur;

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        if (utilisateur != null) {
            nomLabel.setText(utilisateur.getNom());
            prenomLabel.setText(utilisateur.getPrenom());
            emailLabel.setText(utilisateur.getEmail());
            roleLabel.setText(utilisateur.getRole());
        }
    }

    @FXML
    private void editProfile() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/EditProfileDialog.fxml"));
            javafx.scene.control.DialogPane dialogPane = loader.load();
            Controller.EditProfileDialogController controller = loader.getController();
            controller.setUtilisateur(utilisateur);

            javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog = new javafx.scene.control.Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Modifier le Profil");

            java.util.Optional<javafx.scene.control.ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get().getButtonData() == javafx.scene.control.ButtonBar.ButtonData.OK_DONE) {
                Utilisateur updated = controller.getUpdatedUtilisateur();
                // Update in DB
                Service.ServiceUtilisateur service = new Service.ServiceUtilisateur();
                try {
                    boolean success = service.modifier(updated);
                    if (success) {
                        setUtilisateur(updated); // Refresh profile view
                    } else {
                        showError("La mise à jour a échoué.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Erreur lors de la mise à jour: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture de la fenêtre de modification.");
        }
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
