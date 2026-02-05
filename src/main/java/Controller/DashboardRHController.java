package Controller;

import Entite.OffreEmploi;
import Entite.Utilisateur;
import Service.ServiceOffreEmploi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardRHController implements Initializable {

    @FXML private Label userInfoLabel;
    @FXML private TableView<OffreEmploi> offreTable;
    @FXML private TextField titreField;
    @FXML private TextField salaireField;
    @FXML private TextField localisationField;
    @FXML private TextField profilField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> typeContratCombo;
    @FXML private ComboBox<String> statutCombo;

    private ServiceOffreEmploi serviceOffre = new ServiceOffreEmploi();
    private ObservableList<OffreEmploi> offreList = FXCollections.observableArrayList();
    private Utilisateur currentUser;
    private OffreEmploi selectedOffre;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ComboBoxes
        typeContratCombo.setItems(FXCollections.observableArrayList("CDI", "CDD", "Stage", "Freelance", "Alternance"));
        statutCombo.setItems(FXCollections.observableArrayList("Ouverte", "Fermée", "En cours"));

        // Load data
        loadOffres();

        // Table selection listener
        offreTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedOffre = newVal;
                populateForm(newVal);
            }
        });
    }

    public void setUserInfo(Utilisateur user) {
        this.currentUser = user;
        if (user != null) {
            userInfoLabel.setText("Dashboard RH - " + user.getPrenom() + " " + user.getNom());
        }
    }

    private void loadOffres() {
        offreList.clear();
        offreList.addAll(serviceOffre.afficher());
        offreTable.setItems(offreList);
    }

    private void populateForm(OffreEmploi o) {
        titreField.setText(o.getTitre());
        descriptionArea.setText(o.getDescription());
        profilField.setText(o.getProfilRecherche());
        typeContratCombo.setValue(o.getTypeContrat());
        salaireField.setText(String.valueOf(o.getSalaire()));
        localisationField.setText(o.getLocalisation());
        statutCombo.setValue(o.getStatut());
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) return;

        OffreEmploi offre = new OffreEmploi();
        offre.setTitre(titreField.getText());
        offre.setDescription(descriptionArea.getText());
        offre.setProfilRecherche(profilField.getText());
        offre.setTypeContrat(typeContratCombo.getValue());
        offre.setSalaire(Double.parseDouble(salaireField.getText()));
        offre.setLocalisation(localisationField.getText());
        offre.setDatePublication(Date.valueOf(LocalDate.now()));
        offre.setStatut(statutCombo.getValue());
        offre.setRh(currentUser);

        if (serviceOffre.ajouter(offre)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre ajoutée avec succès.");
            loadOffres();
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout de l'offre.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedOffre == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une offre à modifier.");
            return;
        }
        if (!validateForm()) return;

        selectedOffre.setTitre(titreField.getText());
        selectedOffre.setDescription(descriptionArea.getText());
        selectedOffre.setProfilRecherche(profilField.getText());
        selectedOffre.setTypeContrat(typeContratCombo.getValue());
        selectedOffre.setSalaire(Double.parseDouble(salaireField.getText()));
        selectedOffre.setLocalisation(localisationField.getText());
        selectedOffre.setStatut(statutCombo.getValue());

        if (serviceOffre.modifier(selectedOffre)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre modifiée avec succès.");
            loadOffres();
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la modification.");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedOffre == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner une offre à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cette offre?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (serviceOffre.supprimer(selectedOffre.getIdOffre())) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre supprimée avec succès.");
                    loadOffres();
                    handleClear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression.");
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        titreField.clear();
        descriptionArea.clear();
        profilField.clear();
        typeContratCombo.setValue(null);
        salaireField.clear();
        localisationField.clear();
        statutCombo.setValue(null);
        selectedOffre = null;
        offreTable.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (titreField.getText().isEmpty() || typeContratCombo.getValue() == null ||
            salaireField.getText().isEmpty() || localisationField.getText().isEmpty() ||
            statutCombo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez remplir tous les champs obligatoires.");
            return false;
        }
        try {
            Double.parseDouble(salaireField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le salaire doit être un nombre valide.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
