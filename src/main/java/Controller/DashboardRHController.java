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

public class DashboardRHController implements Initializable {
    @FXML private StackPane contentArea;
    @FXML private Label userInfoLabel;
    @FXML private VBox welcomeView;

    @FXML private TableView<OffreEmploi> offreTable;
    @FXML private TextField titreField, salaireField, localisationField, profilField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> typeContratCombo, statutCombo;

    @FXML private TableView<Candidature> candidaturesTable;
    @FXML private TableColumn<Candidature, String> candidatNameCol;
    @FXML private TableColumn<Candidature, String> candidatEmailCol;
    @FXML private TableColumn<Candidature, String> candidatureStatusCol;
    @FXML private TableColumn<Candidature, String> datePostulationCol;

    private final ServiceOffreEmploi serviceOffre = new ServiceOffreEmploi();
    private final ServiceCandidature serviceCandidature = new ServiceCandidature();
    private ObservableList<OffreEmploi> offreList = FXCollections.observableArrayList();
    private ObservableList<Candidature> candidaturesList = FXCollections.observableArrayList();
    private Utilisateur currentUser;
    private OffreEmploi selectedOffre;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupOffreTable();
        setupCandidaturesTable();
        loadOffres();
    }

    public void setUserInfo(Utilisateur user) {
        this.currentUser = user;
        if (user != null) {
            userInfoLabel.setText("Dashboard RH - " + user.getPrenom() + " " + user.getNom());
        }
    }

    private void setupOffreTable() {
        // Basic setup for offretable could be added here if needed, 
        // but it's typically done in FXML or via PropertyValueFactory
        offreTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedOffre = newVal;
                populateForm(newVal);
                loadCandidaturesForOffre(newVal);
            }
        });
        
        typeContratCombo.setItems(FXCollections.observableArrayList("CDI", "CDD", "Stage", "Freelance", "Alternance"));
        statutCombo.setItems(FXCollections.observableArrayList("Ouverte", "Fermée", "En cours"));
    }

    private void setupCandidaturesTable() {
        candidatNameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCandidat() != null ? 
                cellData.getValue().getCandidat().getPrenom() + " " + cellData.getValue().getCandidat().getNom() : 
                "N/A"));
        candidatEmailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCandidat() != null ? cellData.getValue().getCandidat().getEmail() : "N/A"));
        candidatureStatusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatut()));
        datePostulationCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDatePostulation() != null ? cellData.getValue().getDatePostulation().toString() : "N/A"));
        
        candidaturesTable.setItems(candidaturesList);
    }

    private void loadCandidaturesForOffre(OffreEmploi offre) {
        candidaturesList.clear();
        if (offre != null) {
            candidaturesList.addAll(serviceCandidature.afficherByOffreId(offre.getIdOffre()));
        }
    }

    @FXML
    private void showCongesManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CongesView.fxml"));
            Parent congesRoot = loader.load();
            CongesController controller = loader.getController();
            controller.setLoggedInUser(currentUser);
            contentArea.getChildren().setAll(congesRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showOffres() {
        contentArea.getChildren().setAll(welcomeView);
    }

    @FXML
    private void showProjects() {
        loadViewWithRole("/view/ProjectView.fxml", "RH");
    }

    @FXML
    private void showSprints() {
        loadViewWithRole("/view/SprintView.fxml", "RH");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Object controller = loader.getController();
            if (controller instanceof ProjectController) ((ProjectController) controller).setUserRole(role);
            else if (controller instanceof SprintController) ((SprintController) controller).setUserRole(role);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
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
        OffreEmploi o = new OffreEmploi();
        o.setTitre(titreField.getText());
        o.setDescription(descriptionArea.getText());
        o.setProfilRecherche(profilField.getText());
        o.setTypeContrat(typeContratCombo.getValue());
        o.setSalaire(Double.parseDouble(salaireField.getText()));
        o.setLocalisation(localisationField.getText());
        o.setDatePublication(Date.valueOf(LocalDate.now()));
        o.setStatut(statutCombo.getValue());
        o.setRh(currentUser);
        if (serviceOffre.ajouter(o)) {
            loadOffres();
            handleClear();
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedOffre == null || !validateForm()) return;
        selectedOffre.setTitre(titreField.getText());
        selectedOffre.setDescription(descriptionArea.getText());
        selectedOffre.setProfilRecherche(profilField.getText());
        selectedOffre.setTypeContrat(typeContratCombo.getValue());
        selectedOffre.setSalaire(Double.parseDouble(salaireField.getText()));
        selectedOffre.setLocalisation(localisationField.getText());
        selectedOffre.setStatut(statutCombo.getValue());
        if (serviceOffre.modifier(selectedOffre)) {
            loadOffres();
            handleClear();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedOffre == null) return;
        if (serviceOffre.supprimer(selectedOffre.getIdOffre())) {
            loadOffres();
            candidaturesList.clear();
            handleClear();
        }
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
        candidaturesList.clear();
        offreTable.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        return !titreField.getText().isEmpty() && typeContratCombo.getValue() != null && !salaireField.getText().isEmpty();
    }
}
