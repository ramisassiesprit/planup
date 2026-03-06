package Controller;

import Entite.Conge;
import Entite.Utilisateur;
import Service.CongeService;
import Service.ICongeService;
import Service.ServiceUtilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CongesController {

    @FXML private TextField txtCinEmploye, txtNbrJours, txtDjon;
    @FXML private ComboBox<String> cbType, filterStatutCombo;
    @FXML private DatePicker dpDateDebut, dpDateFin;
    @FXML private TableView<Conge> congesTable;
    @FXML private TableColumn<Conge, Integer> idCongeColumn;
    @FXML private TableColumn<Conge, String> employeColumn, typeColumn, statutColumn;
    @FXML private TableColumn<Conge, java.sql.Date> dateDebutColumn, dateFinColumn;
    @FXML private TableColumn<Conge, Integer> nbrJoursColumn;
    @FXML private Label lblSoldeCalculated, errCin, errType, errDebut, errFin, errJustificatif;
    @FXML private Button btnDemander, btnAnnuler, btnAccepter, btnRefuser;

    private final ICongeService congeService = new CongeService();
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private ObservableList<Conge> congeList = FXCollections.observableArrayList();
    private Utilisateur currentUser;

    @FXML
    public void initialize() {
        setupTable();
        setupForm();
    }

    public void setLoggedInUser(Utilisateur user) {
        this.currentUser = user;
        updateButtonVisibility();
        
        // Pre-fill CIN if it's a regular employee requesting
        if (user != null && !user.getRole().equalsIgnoreCase("RH") && !user.getRole().equalsIgnoreCase("ADMIN")) {
            txtCinEmploye.setText(String.valueOf(user.getCin()));
            txtCinEmploye.setEditable(false);
            updateSoldeDisplay(user.getCin());
        }
        
        // Refresh list to apply privacy filters based on the set user
        refreshConges();
    }

    private void setupTable() {
        idCongeColumn.setCellValueFactory(new PropertyValueFactory<>("idConge"));
        employeColumn.setCellValueFactory(cellData -> {
            Utilisateur u = cellData.getValue().getUtilisateur();
            return new SimpleStringProperty(u != null ? u.getNom() + " " + u.getPrenom() + " (" + u.getCin() + ")" : "Inconnu");
        });
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        nbrJoursColumn.setCellValueFactory(new PropertyValueFactory<>("nbrJours"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        congesTable.setItems(congeList);
    }

    private void setupForm() {
        cbType.setItems(FXCollections.observableArrayList("ANNUEL", "MALADIE", "SANS_SOLDE", "FORMATION", "AUTRE..."));
        filterStatutCombo.setItems(FXCollections.observableArrayList("Tous", "EN_ATTENTE", "ACCEPTE", "REFUSE", "ANNULE"));
        filterStatutCombo.setValue("Tous");

        cbType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("AUTRE...".equals(newVal)) {
                cbType.getEditor().clear();
            }
        });

        dpDateDebut.valueProperty().addListener((obs, old, newVal) -> updateNbrJours());
        dpDateFin.valueProperty().addListener((obs, old, newVal) -> updateNbrJours());

        txtCinEmploye.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && newVal.matches("\\d+")) {
                try {
                    int cin = Integer.parseInt(newVal);
                    Utilisateur u = serviceUtilisateur.findByCin(cin);
                    if (u != null) {
                        updateSoldeDisplay(cin);
                    } else {
                        lblSoldeCalculated.setText("--");
                    }
                } catch (NumberFormatException e) {
                    lblSoldeCalculated.setText("--");
                }
            } else {
                lblSoldeCalculated.setText("--");
            }
        });
    }

    private void updateSoldeDisplay(int cin) {
        int solde = congeService.calculateSoldeRestant(cin);
        lblSoldeCalculated.setText(solde + " jours");
    }

    private void updateNbrJours() {
        LocalDate start = dpDateDebut.getValue();
        LocalDate end = dpDateFin.getValue();
        if (start != null && end != null && end.isAfter(start)) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
            txtNbrJours.setText(String.valueOf(days));
        } else {
            txtNbrJours.clear();
        }
    }

    @FXML
    private void handleUploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un justificatif");
        File selectedFile = fileChooser.showOpenDialog(txtDjon.getScene().getWindow());
        if (selectedFile != null) {
            txtDjon.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void demanderConge() {
        if (!validateCongeForm()) return;

        try {
            int cin = Integer.parseInt(txtCinEmploye.getText());
            Utilisateur user = serviceUtilisateur.findByCin(cin);
            
            int nbrJours = Integer.parseInt(txtNbrJours.getText());
            int soldeDispo = congeService.calculateSoldeRestant(cin);

            if (!cbType.getValue().equals("SANS_SOLDE") && nbrJours > soldeDispo) {
                showAlert(Alert.AlertType.WARNING, "Solde insuffisant", "Vous n'avez pas assez de jours de congé.");
                return;
            }

            Conge conge = new Conge();
            conge.setUtilisateur(user);
            
            String type = cbType.getValue();
            if (type == null || type.isEmpty()) {
                type = cbType.getEditor().getText();
            }
            conge.setType(type);
            conge.setDateDebut(java.sql.Date.valueOf(dpDateDebut.getValue()));
            conge.setDateFin(java.sql.Date.valueOf(dpDateFin.getValue()));
            conge.setNbrJours(nbrJours);
            conge.setJustificatif(txtDjon.getText());
            conge.setStatut("EN_ATTENTE");

            if (congeService.demanderConge(conge)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande envoyée avec succès.");
                refreshConges();
                resetForm();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'envoyer la demande.");
        }
    }

    @FXML
    private void accepterConge() {
        Conge selected = congesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            if (congeService.accepterConge(selected.getIdConge(), currentUser.getCin(), "Approuvé")) {
                refreshConges();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void refuserConge() {
        Conge selected = congesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            if (congeService.refuserConge(selected.getIdConge(), currentUser.getCin(), "Refusé")) {
                refreshConges();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void annulerConge() {
        Conge selected = congesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            boolean isRHOrAdmin = currentUser != null && (currentUser.getRole().equalsIgnoreCase("RH") || currentUser.getRole().equalsIgnoreCase("ADMIN"));
            int cinToCheck = isRHOrAdmin ? -1 : currentUser.getCin();
            
            if (congeService.annulerConge(selected.getIdConge(), cinToCheck)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le congé a été annulé.");
                refreshConges();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'annuler le congé. Vérifiez s'il est encore en attente.");
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur technique est survenue.");
        }
    }

    @FXML
    private void filterConges() {
        String filter = filterStatutCombo.getValue();
        if (filter == null || filter.equals("Tous")) {
            congesTable.setItems(congeList);
        } else {
            ObservableList<Conge> filtered = congeList.stream()
                    .filter(c -> c.getStatut().equals(filter))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            congesTable.setItems(filtered);
        }
    }

    @FXML
    public void refreshConges() {
        try {
            List<Conge> list = congeService.listerConges();
            
            // Privacy filter: Regular users only see their own requests
            boolean isRHOrAdmin = currentUser != null && (currentUser.getRole().equalsIgnoreCase("RH") || currentUser.getRole().equalsIgnoreCase("ADMIN"));
            if (!isRHOrAdmin && currentUser != null) {
                list = list.stream()
                        .filter(c -> c.getUtilisateur() != null && c.getUtilisateur().getCin() == currentUser.getCin())
                        .collect(Collectors.toList());
            }
            
            congeList.setAll(list);
            filterConges();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateCongeForm() {
        resetErrors();
        boolean valid = true;

        if (txtCinEmploye.getText().isEmpty()) {
            errCin.setText("CIN requis");
            errCin.setVisible(true);
            valid = false;
        } else {
            Utilisateur u = serviceUtilisateur.findByCin(Integer.parseInt(txtCinEmploye.getText()));
            if (u == null) {
                errCin.setText("Employé non trouvé");
                errCin.setVisible(true);
                valid = false;
            }
        }

        String typeVal = cbType.getValue();
        String typeTxt = cbType.getEditor().getText();
        if ((typeVal == null || typeVal.isEmpty()) && (typeTxt == null || typeTxt.trim().isEmpty())) {
            errType.setText("Type requis");
            errType.setVisible(true);
            valid = false;
        }

        if (dpDateDebut.getValue() == null) {
            errDebut.setText("Date début requise");
            errDebut.setVisible(true);
            valid = false;
        } else if (dpDateDebut.getValue().isBefore(LocalDate.now())) {
            errDebut.setText("La date doit être aujourd'hui ou dans le futur");
            errDebut.setVisible(true);
            valid = false;
        }
        
        if (dpDateFin.getValue() == null) {
            errFin.setText("Date fin requise");
            errFin.setVisible(true);
            valid = false;
        } else if (dpDateDebut.getValue() != null && !dpDateFin.getValue().isAfter(dpDateDebut.getValue())) {
            errFin.setText("La date de fin doit être strictement après le début");
            errFin.setVisible(true);
            valid = false;
        }

        if (txtDjon.getText().isEmpty()) {
            errJustificatif.setText("Justificatif requis (texte ou fichier)");
            errJustificatif.setVisible(true);
            valid = false;
        }

        return valid;
    }

    private void resetErrors() {
        errCin.setVisible(false);
        errType.setVisible(false);
        errDebut.setVisible(false);
        errFin.setVisible(false);
        errJustificatif.setVisible(false);
    }

    private void resetForm() {
        if (currentUser == null || currentUser.getRole().equalsIgnoreCase("RH") || currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            txtCinEmploye.clear();
        }
        cbType.setValue(null);
        dpDateDebut.setValue(null);
        dpDateFin.setValue(null);
        txtNbrJours.clear();
        txtDjon.clear();
    }

    private void updateButtonVisibility() {
        boolean isRHOrAdmin = currentUser != null && (currentUser.getRole().equalsIgnoreCase("RH") || currentUser.getRole().equalsIgnoreCase("ADMIN"));
        
        if (btnAccepter != null) btnAccepter.setVisible(isRHOrAdmin);
        if (btnRefuser != null) btnRefuser.setVisible(isRHOrAdmin);
        if (btnAnnuler != null) btnAnnuler.setVisible(isRHOrAdmin);
        
        if (btnDemander != null) btnDemander.setVisible(true);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
