package Controller;

import Entite.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class CreateEditUserDialogController {
    @FXML
    public TextField cinField;
    @FXML
    public TextField nomField;
    @FXML
    public TextField prenomField;
    @FXML
    public TextField emailField;
    @FXML
    public PasswordField motDePasseField;
    @FXML
    public TextField telField;
    @FXML
    public ComboBox<String> roleComboBox;

    public void setUser(Utilisateur user) {
        if (user != null) {
            cinField.setText(String.valueOf(user.getCin()));
            nomField.setText(user.getNom());
            prenomField.setText(user.getPrenom());
            emailField.setText(user.getEmail());
            motDePasseField.setText(user.getMotDePasse());
            telField.setText(user.getNumTel());
            roleComboBox.setValue(user.getRole());
        }
    }

    public Utilisateur getUser() {
        StringBuilder errors = new StringBuilder();
        int cin = 0;
        try {
            cin = Integer.parseInt(cinField.getText().trim());
            if (cin <= 0)
                errors.append("CIN doit être un entier positif.\n");
        } catch (NumberFormatException e) {
            errors.append("CIN doit être un entier valide.\n");
        }
        if (nomField.getText().trim().isEmpty())
            errors.append("Nom est requis.\n");
        if (prenomField.getText().trim().isEmpty())
            errors.append("Prénom est requis.\n");
        if (emailField.getText().trim().isEmpty())
            errors.append("Email est requis.\n");
        if (motDePasseField.getText().trim().isEmpty())
            errors.append("Mot de passe est requis.\n");
        if (telField.getText().trim().isEmpty())
            errors.append("Téléphone est requis.\n");
        if (roleComboBox.getValue() == null || roleComboBox.getValue().trim().isEmpty())
            errors.append("Rôle est requis.\n");

        if (errors.length() > 0) {
            Alert alert = new Alert(AlertType.ERROR, errors.toString());
            alert.setHeaderText("Champs invalides");
            alert.showAndWait();
            return null;
        }
        return new Utilisateur(
                cin,
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                motDePasseField.getText().trim(),
                telField.getText().trim(),
                roleComboBox.getValue().trim());
    }
}