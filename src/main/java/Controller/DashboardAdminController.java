package Controller;

import Entite.Utilisateur;
import Service.ServiceUtilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class DashboardAdminController {
    @FXML
    private void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfileView.fxml"));
            Parent profileRoot = loader.load();
            ProfileController controller = loader.getController();
            controller.setUtilisateur(loggedInAdmin);
            contentArea.getChildren().setAll(profileRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private javafx.scene.control.ComboBox<String> roleFilterComboBox;

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox mainUserView;

    @FXML
    private Label userInfoLabel;

    @FXML
    private TableView<Utilisateur> usersTable;

    @FXML
    private TableColumn<Utilisateur, Integer> cinColumn;

    @FXML
    private TableColumn<Utilisateur, String> nomColumn;

    @FXML
    private TableColumn<Utilisateur, String> prenomColumn;

    @FXML
    private TableColumn<Utilisateur, String> emailColumn;

    @FXML
    private TableColumn<Utilisateur, String> telColumn;

    @FXML
    private TableColumn<Utilisateur, String> roleColumn;

    @FXML
    private TableColumn<Utilisateur, Void> editColumn;
    @FXML
    private TableColumn<Utilisateur, Void> deleteColumn;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    private Utilisateur loggedInAdmin;

    public void setUserInfo(Utilisateur user) {
        if (user != null) {
            loggedInAdmin = user;
            userInfoLabel.setText("Dashboard ADMIN - " + user.getPrenom() + " " + user.getNom());
            refreshUsersTable();
        }
    }

    private void refreshUsersTable() {
        List<Utilisateur> users = serviceUtilisateur.afficher();
        if (loggedInAdmin != null) {
            users.removeIf(u -> u.getCin() == loggedInAdmin.getCin());
        }
        String selectedRole = roleFilterComboBox != null ? roleFilterComboBox.getValue() : null;
        if (selectedRole != null && !selectedRole.isEmpty() && !selectedRole.equals("Tous les rôles")) {
            users.removeIf(u -> !selectedRole.equals(u.getRole()));
        }
        ObservableList<Utilisateur> data = FXCollections.observableArrayList(users);
        usersTable.setItems(data);
    }

    @FXML
    private void initialize() {
        // Setup role filter ComboBox
        if (roleFilterComboBox != null) {
            roleFilterComboBox.getItems().setAll("Tous les rôles", "ADMIN", "MANAGER", "DEVELOPPEUR", "INTEGRATEUR",
                    "RH", "EMPLOYE", "CANDIDAT");
            roleFilterComboBox.setValue("Tous les rôles");
            roleFilterComboBox.setOnAction(e -> refreshUsersTable());
        }
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telColumn.setCellValueFactory(new PropertyValueFactory<>("numTel"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        editColumn.setCellFactory(col -> new TableCell<Utilisateur, Void>() {
            private final Button editBtn = new Button("Editer");
            {
                editBtn.setOnAction(e -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    showEditUserDialog(user);
                });
                editBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editBtn);
                }
            }
        });

        deleteColumn.setCellFactory(col -> new TableCell<Utilisateur, Void>() {
            private final Button deleteBtn = new Button("Supprimer");
            {
                deleteBtn.setOnAction(e -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet utilisateur?",
                            ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText(null);
                    alert.showAndWait().ifPresent(type -> {
                        if (type == ButtonType.YES) {
                            try {
                                serviceUtilisateur.supprimer(user.getCin());
                                refreshUsersTable();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                showAlert(AlertType.ERROR, "Erreur", "Suppression échouée", ex.getMessage());
                            }
                        }
                    });
                });
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
    }

    @FXML
    private void onCreateUser() {
        showEditUserDialog(null);
    }

    private void showEditUserDialog(Utilisateur user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateEditUserDialog.fxml"));
            DialogPane dialogPane = loader.load();
            CreateEditUserDialogController controller = loader.getController();
            if (user != null) {
                controller.setUser(user);
            }
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(user == null ? "Créer Utilisateur" : "Editer Utilisateur");
            controller.roleComboBox.getItems().setAll("ADMIN", "MANAGER", "DEVELOPPEUR", "INTEGRATEUR", "RH",
                    "EMPLOYE", "CANDIDAT");
            dialog.showAndWait().ifPresent(result -> {
                if (result.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    Utilisateur edited = controller.getUser();
                    if (edited == null)
                        return;
                    boolean success;
                    if (user == null) {
                        try {
                            success = serviceUtilisateur.ajouter(edited);
                            if (!success) {
                                Alert alert = new Alert(AlertType.ERROR,
                                        "Erreur lors de l'ajout de l'utilisateur. Vérifiez que le CIN et l'email sont uniques.");
                                alert.setHeaderText("Ajout échoué");
                                alert.showAndWait();
                                return;
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            showAlert(AlertType.ERROR, "Erreur", "Ajout échoué", ex.getMessage());
                            return;
                        }
                    } else {
                        try {
                            success = serviceUtilisateur.modifier(edited);
                            if (!success) {
                                Alert alert = new Alert(AlertType.ERROR,
                                        "Erreur lors de la modification de l'utilisateur.");
                                alert.setHeaderText("Modification échouée");
                                alert.showAndWait();
                                return;
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            showAlert(AlertType.ERROR, "Erreur", "Modification échouée", ex.getMessage());
                            return;
                        }
                    }
                    refreshUsersTable();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showProjects() {
        loadViewWithRole("/view/ProjectView.fxml", "ADMIN");
    }

    @FXML
    private void showSprints() {
        loadViewWithRole("/view/SprintView.fxml", "ADMIN");
    }

    @FXML
    private void showUsers() {
        contentArea.getChildren().setAll(mainUserView);
        refreshUsersTable();
    }

    @FXML
    private void showTasks() {
        loadViewWithRole("/view/TaskView.fxml", "ADMIN");
    }

    @FXML
    private void showCongesManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CongesView.fxml"));
            Parent congesRoot = loader.load();
            CongesController controller = loader.getController();
            controller.setLoggedInUser(loggedInAdmin);
            contentArea.getChildren().setAll(congesRoot);
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
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Le fichier FXML n'a pas été trouvé : " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ProjectController) {
                ((ProjectController) controller).setUserRole(role);
            } else if (controller instanceof SprintController) {
                ((SprintController) controller).setUserRole(role);
            } else if (controller instanceof TaskController) {
                ((TaskController) controller).setRoleAndUser(role, loggedInAdmin);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
