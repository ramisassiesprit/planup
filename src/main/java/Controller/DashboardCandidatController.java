package Controller;

import Entite.Candidature;
import Entite.OffreEmploi;
import Entite.Utilisateur;
import Service.ServiceCandidature;
import Service.ServiceOffreEmploi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardCandidatController implements Initializable {
    @FXML private StackPane contentArea;
    @FXML private Label userInfoLabel;
    @FXML private VBox welcomeView;
    @FXML private VBox mesCandidaturesView;

    @FXML private TableView<OffreEmploi> offresTable;
    @FXML private TableColumn<OffreEmploi, String> titreCol;
    @FXML private TableColumn<OffreEmploi, String> entrepriseCol;
    @FXML private TableColumn<OffreEmploi, Double> salaireCol;
    @FXML private TableColumn<OffreEmploi, String> typeContratCol;
    @FXML private TableColumn<OffreEmploi, String> localisationCol;

    @FXML private TableView<Candidature> mesCandidaturesTable;
    @FXML private TableColumn<Candidature, String> offreTitleCol;
    @FXML private TableColumn<Candidature, String> candidatureStatusCol;
    @FXML private TableColumn<Candidature, String> datePostulationCol;

    @FXML private TextArea descriptionArea;
    @FXML private Label detailsSalaireLabel, detailsTypeContratLabel, detailsLocalisationLabel;
    @FXML private TextArea lettreMotivationArea;
    @FXML private Button accepterButton, refuserButton, candidaterButton;

    private final ServiceOffreEmploi serviceOffre = new ServiceOffreEmploi();
    private final ServiceCandidature serviceCandidature = new ServiceCandidature();
    private ObservableList<OffreEmploi> offeresList = FXCollections.observableArrayList();
    private ObservableList<Candidature> mesCandidaturesList = FXCollections.observableArrayList();
    private Utilisateur currentUser;
    private OffreEmploi selectedOffre;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupOffresTable();
        setupMesCandidaturesTable();
        loadOffres();
    }

    public void setUserInfo(Utilisateur user) {
        this.currentUser = user;
        if (user != null) {
            userInfoLabel.setText("Dashboard Candidat - " + user.getPrenom() + " " + user.getNom());
            loadMesCandidatures();
        }
    }

    private void setupOffresTable() {
        titreCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitre()));
        entrepriseCol.setCellValueFactory(cellData -> {
            OffreEmploi offre = cellData.getValue();
            String entreprise = offre.getRh() != null ? offre.getRh().getNom() : "Non spécifié";
            return new javafx.beans.property.SimpleStringProperty(entreprise);
        });
        salaireCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getSalaire()).asObject());
        typeContratCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTypeContrat()));
        localisationCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLocalisation()));

        offresTable.setItems(offeresList);
        offresTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedOffre = newVal;
                updateOffreDetails(newVal);
                updateCandidatureStatusForOffre(newVal);
            }
        });
    }

    private void setupMesCandidaturesTable() {
        offreTitleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getOffre() != null ? cellData.getValue().getOffre().getTitre() : "N/A"));
        candidatureStatusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatut()));
        datePostulationCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDatePostulation() != null ? cellData.getValue().getDatePostulation().toString() : "N/A"));

        mesCandidaturesTable.setItems(mesCandidaturesList);
    }

    private void loadOffres() {
        offeresList.clear();
        offeresList.addAll(serviceOffre.afficher());
    }

    private void loadMesCandidatures() {
        mesCandidaturesList.clear();
        if (currentUser != null) {
            mesCandidaturesList.addAll(serviceCandidature.afficherByCandidatCin(currentUser.getCin()));
        }
    }

    private void updateOffreDetails(OffreEmploi offre) {
        descriptionArea.setText(offre.getDescription());
        detailsSalaireLabel.setText("Salaire: " + offre.getSalaire() + " TND");
        detailsTypeContratLabel.setText("Type Contrat: " + offre.getTypeContrat());
        detailsLocalisationLabel.setText("Localisation: " + offre.getLocalisation());
    }

    private void updateCandidatureStatusForOffre(OffreEmploi offre) {
        if (currentUser == null || offre == null) {
            candidaterButton.setDisable(false);
            accepterButton.setDisable(true);
            refuserButton.setDisable(true);
            lettreMotivationArea.clear();
            return;
        }

        Candidature existingCandidature = serviceCandidature.getCandidatureByCanidatAndOffre(currentUser.getCin(), offre.getIdOffre());
        
        if (existingCandidature != null) {
            candidaterButton.setDisable(true);
            String status = existingCandidature.getStatut();
            
            if ("PENDING".equalsIgnoreCase(status)) {
                accepterButton.setDisable(false);
                refuserButton.setDisable(false);
            } else {
                accepterButton.setDisable(true);
                refuserButton.setDisable(true);
            }
            
            lettreMotivationArea.setText(existingCandidature.getLettreMotivation());
        } else {
            candidaterButton.setDisable(false);
            accepterButton.setDisable(true);
            refuserButton.setDisable(true);
            lettreMotivationArea.clear();
        }
    }

    @FXML
    private void handleCandidater() {
        if (selectedOffre == null || currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Candidature", "Veuillez sélectionner une offre.");
            return;
        }

        String lettre = lettreMotivationArea.getText();
        if (lettre.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Lettre de Motivation", "Veuillez entrer une lettre de motivation.");
            return;
        }

        Candidature candidature = new Candidature(
                currentUser,
                selectedOffre,
                "PENDING",
                new Date(System.currentTimeMillis()),
                lettre
        );

        if (serviceCandidature.ajouter(candidature)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Candidature", "Votre candidature a été enregistrée.");
            loadMesCandidatures();
            updateCandidatureStatusForOffre(selectedOffre);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Candidature", "Erreur lors de l'enregistrement de la candidature.");
        }
    }

    @FXML
    private void handleAccepter() {
        if (selectedOffre == null || currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Action", "Veuillez sélectionner une offre.");
            return;
        }

        Candidature candidature = serviceCandidature.getCandidatureByCanidatAndOffre(currentUser.getCin(), selectedOffre.getIdOffre());
        if (candidature != null && serviceCandidature.updateStatut(candidature.getIdCandidature(), "ACCEPTED")) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Acceptation", "Vous avez accepté cette offre.");
            loadMesCandidatures();
            updateCandidatureStatusForOffre(selectedOffre);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Action", "Erreur lors de l'acceptation de l'offre.");
        }
    }

    @FXML
    private void handleRefuser() {
        if (selectedOffre == null || currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Action", "Veuillez sélectionner une offre.");
            return;
        }

        Candidature candidature = serviceCandidature.getCandidatureByCanidatAndOffre(currentUser.getCin(), selectedOffre.getIdOffre());
        if (candidature != null && serviceCandidature.updateStatut(candidature.getIdCandidature(), "DECLINED")) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Refus", "Vous avez décliné cette offre.");
            loadMesCandidatures();
            updateCandidatureStatusForOffre(selectedOffre);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Action", "Erreur lors du refus de l'offre.");
        }
    }

    @FXML
    private void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileView.fxml"));
            Parent profileRoot = loader.load();
            ProfileController controller = loader.getController();
            controller.setUtilisateur(currentUser);
            contentArea.getChildren().setAll(profileRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showOffres() {
        contentArea.getChildren().setAll(welcomeView);
    }

    @FXML
    private void showMesCandidatures() {
        loadMesCandidatures();
        contentArea.getChildren().setAll(mesCandidaturesView);
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

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
