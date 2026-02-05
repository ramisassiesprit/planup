package Controller;

import Entite.Conge;
import Entite.Utilisateur;
import Service.CongeService;
import Service.ServiceUtilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardRHController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label userInfoLabel;

    // ============ Congé Form Controls ============
    @FXML
    private TextField txtCinEmploye;
    @FXML
    private ComboBox<String> cbType;
    @FXML
    private DatePicker dpDateDebut;
    @FXML
    private DatePicker dpDateFin;
    @FXML
    private TextField txtNbrJours;
    @FXML
    private TextField txtDjon;
    @FXML
    private TextField txtCongeSolde;

    // ============ Conges View ============
    @FXML
    private VBox congesView;
    @FXML
    private TableView<Conge> congesTable;
    @FXML
    private TableColumn<Conge, Integer> idCongeColumn;
    @FXML
    private TableColumn<Conge, String> employeColumn;
    @FXML
    private TableColumn<Conge, String> typeColumn;
    @FXML
    private TableColumn<Conge, String> dateDebutColumn;
    @FXML
    private TableColumn<Conge, String> dateFinColumn;
    @FXML
    private TableColumn<Conge, Integer> nbrJoursColumn;
    @FXML
    private TableColumn<Conge, String> statutColumn;
    @FXML
    private ComboBox<String> filterStatutCombo;

    // ============ Welcome View ============
    @FXML
    private VBox welcomeView;

    private final CongeService congeService = new CongeService();
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private Utilisateur loggedInUser;
    private List<Conge> allConges;
    private String userRole = "RH";

    @FXML
    private void initialize() {
        // Initialize type ComboBox
        if (cbType != null) {
            cbType.setItems(FXCollections.observableArrayList("ANNUEL", "MALADIE", "MATERNITE", "PATERNITE", "SANS_SOLDE"));
            cbType.setValue("ANNUEL");
        }

        if (congesTable != null) {
            setupCongesTable();
            setupFilters();

            // Selection listener: populate form when a congé row is clicked
            congesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        afficherConge(newVal);
                    }
                }
            );
        }
    }

    public void setUserInfo(Utilisateur user) {
        if (user != null) {
            loggedInUser = user;
            userInfoLabel.setText("Dashboard RH - " + user.getPrenom() + " " + user.getNom());
            this.userRole = user.getRole();
        }
    }

    // ============ Table Setup ============

    private void setupCongesTable() {
        idCongeColumn.setCellValueFactory(new PropertyValueFactory<>("idConge"));

        employeColumn.setCellValueFactory(cellData -> {
            Utilisateur u = cellData.getValue().getUtilisateur();
            String name = u != null ? u.getPrenom() + " " + u.getNom() + " (" + u.getCin() + ")" : "N/A";
            return new SimpleStringProperty(name);
        });

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        dateDebutColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDateDebut() != null ?
                cellData.getValue().getDateDebut().toString() : ""));

        dateFinColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDateFin() != null ?
                cellData.getValue().getDateFin().toString() : ""));

        nbrJoursColumn.setCellValueFactory(new PropertyValueFactory<>("nbrJours"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    private void setupFilters() {
        filterStatutCombo.setItems(FXCollections.observableArrayList(
            "Tous", "EN_ATTENTE", "ACCEPTE", "REFUSE", "ANNULE"
        ));
        filterStatutCombo.setValue("Tous");
    }

    // ============ Congé Actions ============

    @FXML
    private void demanderConge() {
        try {
            String cinText = txtCinEmploye.getText().trim();
            if (cinText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champ requis", "Veuillez saisir le CIN de l'employé.");
                return;
            }
            int cin = Integer.parseInt(cinText);

            Utilisateur employe = serviceUtilisateur.findByCin(cin);
            if (employe == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur trouvé avec le CIN: " + cin);
                return;
            }

            String type = cbType.getValue();
            LocalDate debut = dpDateDebut.getValue();
            LocalDate fin = dpDateFin.getValue();
            String nbrJoursText = txtNbrJours.getText().trim();

            if (type == null || debut == null || fin == null || nbrJoursText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir Type, Date début, Date fin et Nbr jours.");
                return;
            }

            int nbrJours = Integer.parseInt(nbrJoursText);
            String justificatif = txtDjon.getText().trim();
            int congeSolde = 0;
            try {
                congeSolde = Integer.parseInt(txtCongeSolde.getText().trim());
            } catch (NumberFormatException ignored) {
            }

            Conge conge = new Conge();
            conge.setUtilisateur(employe);
            conge.setType(type);
            conge.setDateDebut(Date.valueOf(debut));
            conge.setDateFin(Date.valueOf(fin));
            conge.setNbrJours(nbrJours);
            conge.setJustificatif(justificatif);
            conge.setStatut("EN_ATTENTE");
            conge.setCongeSolde(congeSolde);

            if (congeService.demanderConge(conge)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Congé demandé avec succès.");
                clearCongeFields();
                refreshConges();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de créer la demande de congé.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "CIN et Nbr jours doivent être des nombres valides.");
        }
    }

    @FXML
    private void annulerConge() {
        Conge selected = congesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un congé à annuler.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment annuler ce congé ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // Pass -1 to skip owner check (RH can annul any)
                if (congeService.annulerConge(selected.getIdConge(), -1)) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Congé annulé avec succès.");
                    clearCongeFields();
                    refreshConges();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'annuler le congé (seuls les EN_ATTENTE).");
                }
            }
        });
    }

    @FXML
    private void accepterConge() {
        Conge selected = congesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un congé à accepter.");
            return;
        }
        if (loggedInUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non connecté.");
            return;
        }

        if (congeService.accepterConge(selected.getIdConge(), loggedInUser.getCin(), "Approuvé par RH")) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Congé accepté avec succès.");
            clearCongeFields();
            refreshConges();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'accepter le congé (vérifiez votre rôle RH et le statut).");
        }
    }

    @FXML
    private void refuserConge() {
        Conge selected = congesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un congé à refuser.");
            return;
        }
        if (loggedInUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non connecté.");
            return;
        }

        if (congeService.refuserConge(selected.getIdConge(), loggedInUser.getCin(), "Refusé par RH")) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Congé refusé avec succès.");
            clearCongeFields();
            refreshConges();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de refuser le congé (vérifiez votre rôle RH et le statut).");
        }
    }

    @FXML
    private void refreshConges() {
        allConges = congeService.listerConges();
        filterConges();
        clearCongeFields();
    }

    @FXML
    private void filterConges() {
        if (allConges == null) {
            refreshConges();
            return;
        }

        String statut = filterStatutCombo.getValue();
        List<Conge> filtered = allConges;

        if (statut != null && !"Tous".equals(statut)) {
            filtered = allConges.stream()
                .filter(c -> statut.equals(c.getStatut()))
                .collect(Collectors.toList());
        }

        congesTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // ============ Form Helpers ============

    private void afficherConge(Conge c) {
        if (c.getUtilisateur() != null) {
            txtCinEmploye.setText(String.valueOf(c.getUtilisateur().getCin()));
        }
        cbType.setValue(c.getType());
        if (c.getDateDebut() != null) {
            dpDateDebut.setValue(c.getDateDebut().toLocalDate());
        }
        if (c.getDateFin() != null) {
            dpDateFin.setValue(c.getDateFin().toLocalDate());
        }
        txtNbrJours.setText(String.valueOf(c.getNbrJours()));
        txtDjon.setText(c.getJustificatif() != null ? c.getJustificatif() : "");
        txtCongeSolde.setText(String.valueOf(c.getCongeSolde()));
    }

    private void clearCongeFields() {
        txtCinEmploye.clear();
        cbType.setValue("ANNUEL");
        dpDateDebut.setValue(null);
        dpDateFin.setValue(null);
        txtNbrJours.clear();
        txtDjon.clear();
        txtCongeSolde.clear();
        congesTable.getSelectionModel().clearSelection();
    }

    // ============ Navigation ============

    @FXML
    private void showCongesManagement() {
        // Remove any dynamically loaded views
        contentArea.getChildren().removeIf(node -> node != congesView && node != welcomeView);
        
        if (congesView != null) {
            congesView.setVisible(true);
            if (welcomeView != null) welcomeView.setVisible(false);
            refreshConges();
        }
    }

    @FXML
    private void showProjects() {
        if (congesView != null) congesView.setVisible(false);
        if (welcomeView != null) welcomeView.setVisible(false);
        loadViewWithRole("/view/ProjectView.fxml", userRole);
    }

    @FXML
    private void showSprints() {
        if (congesView != null) congesView.setVisible(false);
        if (welcomeView != null) welcomeView.setVisible(false);
        loadViewWithRole("/view/SprintView.fxml", userRole);
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

            // Remove only dynamically loaded views, keep built-in views (congesView, welcomeView)
            contentArea.getChildren().removeIf(node -> node != congesView && node != welcomeView);
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ============ Utility ============

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
