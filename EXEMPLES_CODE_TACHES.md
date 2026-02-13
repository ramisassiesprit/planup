# 💻 Exemples de Code - Module de Gestion de Tâches

## 📑 Table des Matières
1. [Exemples Backend](#exemples-backend)
2. [Exemples Frontend](#exemples-frontend)
3. [Exemples SQL](#exemples-sql)
4. [Tests et Débogage](#tests-et-débogage)
5. [Cas d'usage avancés](#cas-dusage-avancés)

---

## 🔧 Exemples Backend

### 1. Créer une tâche simple

```java
import Entite.Tache;
import Service.ServiceTache;
import java.sql.Date;
import java.sql.SQLException;

public class ExempleCreationTache {
    public static void main(String[] args) {
        ServiceTache service = new ServiceTache();
        
        // Créer une nouvelle tâche
        Tache tache = new Tache();
        tache.setName("Implémenter API REST");
        tache.setDescription("Créer les endpoints pour la gestion des utilisateurs");
        tache.setDateLimite(Date.valueOf("2026-03-15"));
        tache.setDuree(10); // 10 jours
        tache.setPriorite(1); // Haute priorité
        tache.setEstimation(40); // 40 heures
        tache.setStatut("PAS_ENCORE_FAITE");
        
        try {
            if (service.ajouter(tache)) {
                System.out.println("✅ Tâche créée avec succès !");
            } else {
                System.out.println("❌ Échec de la création de la tâche");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### 2. Assigner une tâche à un utilisateur

```java
import Entite.Tache;
import Entite.Utilisateur;
import Entite.Sprint;
import Service.ServiceTache;
import Service.ServiceUtilisateur;
import Service.ServiceSprint;
import java.sql.Date;
import java.sql.SQLException;

public class ExempleAssignationTache {
    public static void main(String[] args) {
        ServiceTache serviceTache = new ServiceTache();
        ServiceUtilisateur serviceUser = new ServiceUtilisateur();
        ServiceSprint serviceSprint = new ServiceSprint();
        
        try {
            // Récupérer un utilisateur (développeur)
            Utilisateur dev = serviceUser.findbyId(12345678);
            
            // Récupérer un sprint
            Sprint sprint = serviceSprint.findbyId(1);
            
            // Créer une tâche avec assignation
            Tache tache = new Tache();
            tache.setName("Corriger bug #123");
            tache.setDescription("Le bouton de connexion ne fonctionne pas");
            tache.setDateLimite(Date.valueOf("2026-02-20"));
            tache.setPriorite(1);
            tache.setEstimation(4);
            tache.setStatut("PAS_ENCORE_FAITE");
            tache.setSprint(sprint);
            tache.setAffecte(dev);
            tache.setDateAffectation(new Date(System.currentTimeMillis()));
            
            if (serviceTache.ajouter(tache)) {
                System.out.println("✅ Tâche créée et assignée à " + dev.getNom());
            }
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

### 3. Récupérer et afficher toutes les tâches

```java
import Entite.Tache;
import Service.ServiceTache;
import java.sql.SQLException;
import java.util.List;

public class ExempleListeTaches {
    public static void main(String[] args) {
        ServiceTache service = new ServiceTache();
        
        try {
            List<Tache> taches = service.readAll();
            
            System.out.println("📋 Liste de toutes les tâches :");
            System.out.println("─".repeat(80));
            
            for (Tache t : taches) {
                System.out.printf("ID: %d | %s | Statut: %s | Assigné à: %s%n",
                    t.getIdTache(),
                    t.getName(),
                    t.getStatut(),
                    t.getAffecte() != null ? t.getAffecte().getNom() : "Non assignée"
                );
            }
            
            System.out.println("─".repeat(80));
            System.out.println("Total : " + taches.size() + " tâche(s)");
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

### 4. Récupérer les tâches d'un développeur

```java
import Entite.Tache;
import Service.ServiceTache;
import java.sql.SQLException;
import java.util.List;

public class ExempleTachesUtilisateur {
    public static void main(String[] args) {
        ServiceTache service = new ServiceTache();
        int cinDeveloppeur = 12345678; // CIN du développeur
        
        try {
            List<Tache> mesTaches = service.findByAssignee(cinDeveloppeur);
            
            System.out.println("📌 Mes tâches :");
            System.out.println("─".repeat(80));
            
            // Grouper par statut
            long pasEncoreFaites = mesTaches.stream()
                .filter(t -> "PAS_ENCORE_FAITE".equals(t.getStatut()))
                .count();
            long enCours = mesTaches.stream()
                .filter(t -> "EN_COURS".equals(t.getStatut()))
                .count();
            long terminees = mesTaches.stream()
                .filter(t -> "DEJA_FAITE".equals(t.getStatut()))
                .count();
            
            System.out.println("🔴 Pas encore faites : " + pasEncoreFaites);
            System.out.println("🟡 En cours : " + enCours);
            System.out.println("🟢 Terminées : " + terminees);
            System.out.println("─".repeat(80));
            
            // Afficher les détails
            for (Tache t : mesTaches) {
                String emoji = switch (t.getStatut()) {
                    case "PAS_ENCORE_FAITE" -> "🔴";
                    case "EN_COURS" -> "🟡";
                    case "DEJA_FAITE" -> "🟢";
                    default -> "⚪";
                };
                
                System.out.printf("%s %s (Priorité: %d, Estimation: %dh)%n",
                    emoji, t.getName(), t.getPriorite(), t.getEstimation());
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

### 5. Mettre à jour le statut d'une tâche

```java
import Service.ServiceTache;
import java.sql.SQLException;

public class ExempleChangementStatut {
    public static void main(String[] args) {
        ServiceTache service = new ServiceTache();
        int idTache = 1;
        String nouveauStatut = "EN_COURS";
        
        try {
            if (service.updateStatus(idTache, nouveauStatut)) {
                System.out.println("✅ Statut mis à jour : " + nouveauStatut);
            } else {
                System.out.println("❌ Échec de la mise à jour");
            }
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

### 6. Modifier une tâche complète

```java
import Entite.Tache;
import Service.ServiceTache;
import java.sql.Date;
import java.sql.SQLException;

public class ExempleModificationTache {
    public static void main(String[] args) {
        ServiceTache service = new ServiceTache();
        
        try {
            // Récupérer la tâche existante
            Tache tache = service.findbyId(1);
            
            if (tache != null) {
                // Modifier les propriétés
                tache.setName("Implémenter login (URGENT)");
                tache.setPriorite(1); // Passer en haute priorité
                tache.setDateLimite(Date.valueOf("2026-02-15")); // Nouvelle deadline
                tache.setEstimation(12); // Réviser l'estimation
                
                // Sauvegarder les modifications
                if (service.modifier(tache)) {
                    System.out.println("✅ Tâche modifiée avec succès !");
                    System.out.println(tache);
                }
            } else {
                System.out.println("❌ Tâche non trouvée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

### 7. Supprimer une tâche

```java
import Entite.Tache;
import Service.ServiceTache;
import java.sql.SQLException;

public class ExempleSuppressionTache {
    public static void main(String[] args) {
        ServiceTache service = new ServiceTache();
        
        try {
            // Récupérer la tâche à supprimer
            Tache tache = service.findbyId(1);
            
            if (tache != null) {
                System.out.println("⚠️  Suppression de : " + tache.getName());
                
                if (service.supprimer(tache)) {
                    System.out.println("✅ Tâche supprimée avec succès");
                }
            } else {
                System.out.println("❌ Tâche non trouvée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

---

## 🖥️ Exemples Frontend

### 1. Initialiser le contrôleur avec un rôle

```java
// Dans le Dashboard après connexion
TaskController taskController = loader.getController();
taskController.setRoleAndUser("DEVELOPPEUR", utilisateurConnecte);
```

### 2. Personnaliser l'affichage selon le rôle

```java
public void setRoleAndUser(String role, Utilisateur user) {
    this.userRole = role;
    this.currentUser = user;
    
    // Masquer/afficher les éléments selon le rôle
    switch (role.toUpperCase()) {
        case "ADMIN":
        case "MANAGER":
            formContainer.setVisible(true);
            btnAssign.setVisible(true);
            break;
            
        case "DEVELOPPEUR":
        case "INTEGRATEUR":
            formContainer.setVisible(false);
            btnAssign.setVisible(false);
            // Afficher seulement les tâches assignées
            break;
            
        default:
            // Pas d'accès au module
            showAlert(Alert.AlertType.WARNING, "Accès refusé", 
                     "Vous n'avez pas accès à ce module", "");
            return;
    }
    
    loadData();
}
```

### 3. Ajouter un filtre de recherche

```java
// Ajouter dans TaskController.java
@FXML
private TextField searchField;

@FXML
public void initialize() {
    // ... code existant ...
    
    // Ajouter un listener pour la recherche
    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        filterTasks(newValue);
    });
}

private void filterTasks(String searchText) {
    if (searchText == null || searchText.isEmpty()) {
        taskTable.setItems(taskList);
        return;
    }
    
    ObservableList<Tache> filteredList = FXCollections.observableArrayList();
    
    for (Tache t : taskList) {
        if (t.getName().toLowerCase().contains(searchText.toLowerCase()) ||
            t.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
            filteredList.add(t);
        }
    }
    
    taskTable.setItems(filteredList);
}
```

### 4. Ajouter une colonne pour la date limite

```java
// Dans TaskView.fxml, ajouter :
// <TableColumn fx:id="colDeadline" text="Date limite" prefWidth="120"/>

// Dans TaskController.java
@FXML
private TableColumn<Tache, Date> colDeadline;

@FXML
public void initialize() {
    // ... code existant ...
    
    colDeadline.setCellValueFactory(new PropertyValueFactory<>("dateLimite"));
    
    // Formater la date
    colDeadline.setCellFactory(column -> new TableCell<Tache, Date>() {
        @Override
        protected void updateItem(Date item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.toString());
                
                // Colorer en rouge si la date est dépassée
                if (item.before(new Date(System.currentTimeMillis()))) {
                    setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626;");
                } else {
                    setStyle("");
                }
            }
        }
    });
}
```

### 5. Ajouter une confirmation avant suppression

```java
@FXML
private void handleDelete() {
    Tache selected = taskTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showAlert(Alert.AlertType.WARNING, "Attention", 
                 "Aucune tâche sélectionnée", 
                 "Veuillez sélectionner une tâche à supprimer.");
        return;
    }
    
    // Demander confirmation
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirmation");
    confirmation.setHeaderText("Supprimer la tâche ?");
    confirmation.setContentText("Êtes-vous sûr de vouloir supprimer : " + selected.getName());
    
    Optional<ButtonType> result = confirmation.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
            if (serviceTache.supprimer(selected)) {
                loadData();
                handleClear();
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                         "Tâche supprimée", "La tâche a été supprimée avec succès.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Suppression échouée", e.getMessage());
        }
    }
}
```

### 6. Validation des champs du formulaire

```java
@FXML
private void handleAdd() {
    // Validation
    if (nameField.getText().trim().isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Erreur", 
                 "Champ requis", "Le nom de la tâche est obligatoire.");
        return;
    }
    
    if (deadlinePicker.getValue() == null) {
        showAlert(Alert.AlertType.ERROR, "Erreur", 
                 "Champ requis", "La date limite est obligatoire.");
        return;
    }
    
    // Vérifier que la priorité est entre 1 et 5
    try {
        int priorite = Integer.parseInt(priorityField.getText());
        if (priorite < 1 || priorite > 5) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Valeur invalide", "La priorité doit être entre 1 et 5.");
            return;
        }
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Erreur", 
                 "Format invalide", "La priorité doit être un nombre.");
        return;
    }
    
    // Vérifier que l'estimation est positive
    try {
        int estimation = Integer.parseInt(estimationField.getText());
        if (estimation <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Valeur invalide", "L'estimation doit être positive.");
            return;
        }
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Erreur", 
                 "Format invalide", "L'estimation doit être un nombre.");
        return;
    }
    
    // Si tout est valide, créer la tâche
    try {
        Tache t = new Tache();
        t.setName(nameField.getText().trim());
        t.setDescription(descArea.getText().trim());
        t.setDateLimite(Date.valueOf(deadlinePicker.getValue()));
        t.setPriorite(Integer.parseInt(priorityField.getText()));
        t.setEstimation(Integer.parseInt(estimationField.getText()));
        t.setStatut("PAS_ENCORE_FAITE");
        t.setSprint(sprintCombo.getValue());
        t.setAffecte(assigneeCombo.getValue());
        
        if (t.getAffecte() != null) {
            t.setDateAffectation(new Date(System.currentTimeMillis()));
        }
        
        if (serviceTache.ajouter(t)) {
            loadData();
            handleClear();
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                     "Tâche ajoutée", "La tâche a été créée avec succès.");
        }
    } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR, "Erreur", 
                 "Ajout échoué", e.getMessage());
    }
}
```

---

## 🗄️ Exemples SQL

### 1. Requêtes de base

```sql
-- Récupérer toutes les tâches
SELECT * FROM tache;

-- Récupérer une tâche par ID
SELECT * FROM tache WHERE id_tache = 1;

-- Récupérer les tâches d'un utilisateur
SELECT * FROM tache WHERE cin_affecte = 12345678;

-- Récupérer les tâches d'un sprint
SELECT * FROM tache WHERE id_sprint = 1;

-- Récupérer les tâches par statut
SELECT * FROM tache WHERE statut = 'EN_COURS';
```

### 2. Requêtes avec JOIN

```sql
-- Récupérer les tâches avec les informations de l'utilisateur assigné
SELECT 
    t.id_tache,
    t.name AS tache_nom,
    t.statut,
    u.nom AS utilisateur_nom,
    u.prenom AS utilisateur_prenom
FROM tache t
LEFT JOIN utilisateur u ON t.cin_affecte = u.cin;

-- Récupérer les tâches avec sprint et projet
SELECT 
    t.id_tache,
    t.name AS tache_nom,
    t.statut,
    s.name AS sprint_nom,
    p.name AS projet_nom
FROM tache t
LEFT JOIN sprint s ON t.id_sprint = s.id_sprint
LEFT JOIN project p ON s.id_project = p.id_project;

-- Récupérer toutes les informations liées
SELECT 
    t.*,
    u.nom AS user_nom,
    u.prenom AS user_prenom,
    s.name AS sprint_name,
    p.name AS project_name
FROM tache t
LEFT JOIN utilisateur u ON t.cin_affecte = u.cin
LEFT JOIN sprint s ON t.id_sprint = s.id_sprint
LEFT JOIN project p ON s.id_project = p.id_project;
```

### 3. Statistiques et analyses

```sql
-- Nombre de tâches par statut
SELECT statut, COUNT(*) as nombre
FROM tache
GROUP BY statut;

-- Nombre de tâches par utilisateur
SELECT 
    u.nom,
    u.prenom,
    COUNT(t.id_tache) as nombre_taches
FROM utilisateur u
LEFT JOIN tache t ON u.cin = t.cin_affecte
WHERE u.role IN ('DEVELOPPEUR', 'INTEGRATEUR')
GROUP BY u.cin, u.nom, u.prenom
ORDER BY nombre_taches DESC;

-- Charge de travail par utilisateur (somme des estimations)
SELECT 
    u.nom,
    u.prenom,
    SUM(t.estimation) as heures_totales,
    COUNT(t.id_tache) as nombre_taches
FROM utilisateur u
LEFT JOIN tache t ON u.cin = t.cin_affecte
WHERE t.statut != 'DEJA_FAITE'
GROUP BY u.cin, u.nom, u.prenom
ORDER BY heures_totales DESC;

-- Tâches en retard
SELECT 
    t.id_tache,
    t.name,
    t.date_limite,
    t.statut,
    u.nom AS assignee
FROM tache t
LEFT JOIN utilisateur u ON t.cin_affecte = u.cin
WHERE t.date_limite < CURDATE() 
  AND t.statut != 'DEJA_FAITE';

-- Progression par sprint
SELECT 
    s.name AS sprint,
    COUNT(t.id_tache) as total_taches,
    SUM(CASE WHEN t.statut = 'DEJA_FAITE' THEN 1 ELSE 0 END) as taches_terminees,
    ROUND(SUM(CASE WHEN t.statut = 'DEJA_FAITE' THEN 1 ELSE 0 END) * 100.0 / COUNT(t.id_tache), 2) as pourcentage
FROM sprint s
LEFT JOIN tache t ON s.id_sprint = t.id_sprint
GROUP BY s.id_sprint, s.name;
```

### 4. Requêtes de mise à jour

```sql
-- Changer le statut d'une tâche
UPDATE tache 
SET statut = 'EN_COURS' 
WHERE id_tache = 1;

-- Assigner une tâche à un utilisateur
UPDATE tache 
SET cin_affecte = 12345678, 
    date_affectation = CURDATE() 
WHERE id_tache = 1;

-- Modifier la priorité de toutes les tâches en retard
UPDATE tache 
SET priorite = 1 
WHERE date_limite < CURDATE() 
  AND statut != 'DEJA_FAITE';

-- Réassigner toutes les tâches d'un utilisateur à un autre
UPDATE tache 
SET cin_affecte = 87654321 
WHERE cin_affecte = 12345678 
  AND statut != 'DEJA_FAITE';
```

---

## 🧪 Tests et Débogage

### 1. Test unitaire pour ServiceTache

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Entite.Tache;
import Service.ServiceTache;
import java.sql.Date;

public class ServiceTacheTest {
    
    @Test
    public void testAjouterTache() throws Exception {
        ServiceTache service = new ServiceTache();
        
        Tache tache = new Tache();
        tache.setName("Test Tâche");
        tache.setDescription("Description de test");
        tache.setDateLimite(Date.valueOf("2026-12-31"));
        tache.setPriorite(3);
        tache.setEstimation(5);
        tache.setStatut("PAS_ENCORE_FAITE");
        
        boolean result = service.ajouter(tache);
        assertTrue(result, "La tâche devrait être ajoutée avec succès");
    }
    
    @Test
    public void testRecupererTache() throws Exception {
        ServiceTache service = new ServiceTache();
        
        Tache tache = service.findbyId(1);
        assertNotNull(tache, "La tâche devrait exister");
        assertEquals("Implémenter login", tache.getName());
    }
    
    @Test
    public void testUpdateStatus() throws Exception {
        ServiceTache service = new ServiceTache();
        
        boolean result = service.updateStatus(1, "EN_COURS");
        assertTrue(result, "Le statut devrait être mis à jour");
        
        Tache tache = service.findbyId(1);
        assertEquals("EN_COURS", tache.getStatut());
    }
}
```

### 2. Logging pour le débogage

```java
import java.util.logging.Logger;
import java.util.logging.Level;

public class ServiceTache implements IService<Tache> {
    private static final Logger LOGGER = Logger.getLogger(ServiceTache.class.getName());
    
    @Override
    public boolean ajouter(Tache t) throws SQLException {
        LOGGER.log(Level.INFO, "Tentative d'ajout de tâche : {0}", t.getName());
        
        String query = "INSERT INTO tache (name, description, ...) VALUES (?, ?, ...)";
        try (Connection c = DataSource.getInstance().getCon();
             PreparedStatement ps = c.prepareStatement(query)) {
            
            ps.setString(1, t.getName());
            // ... autres paramètres ...
            
            boolean result = ps.executeUpdate() > 0;
            
            if (result) {
                LOGGER.log(Level.INFO, "Tâche ajoutée avec succès : {0}", t.getName());
            } else {
                LOGGER.log(Level.WARNING, "Échec de l'ajout de la tâche : {0}", t.getName());
            }
            
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de l'ajout de la tâche", e);
            throw e;
        }
    }
}
```

---

## 🚀 Cas d'usage avancés

### 1. Calculer la charge de travail d'un développeur

```java
import Entite.Tache;
import Service.ServiceTache;
import java.sql.SQLException;
import java.util.List;

public class CalculChargeTravail {
    public static void calculerCharge(int cin) {
        ServiceTache service = new ServiceTache();
        
        try {
            List<Tache> taches = service.findByAssignee(cin);
            
            int heuresRestantes = taches.stream()
                .filter(t -> !"DEJA_FAITE".equals(t.getStatut()))
                .mapToInt(Tache::getEstimation)
                .sum();
            
            int heuresTerminees = taches.stream()
                .filter(t -> "DEJA_FAITE".equals(t.getStatut()))
                .mapToInt(Tache::getEstimation)
                .sum();
            
            System.out.println("📊 Charge de travail :");
            System.out.println("  Heures restantes : " + heuresRestantes + "h");
            System.out.println("  Heures terminées : " + heuresTerminees + "h");
            System.out.println("  Total : " + (heuresRestantes + heuresTerminees) + "h");
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

### 2. Générer un rapport de sprint

```java
import Entite.Tache;
import Service.ServiceTache;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RapportSprint {
    public static void genererRapport(int idSprint) {
        ServiceTache service = new ServiceTache();
        
        try {
            List<Tache> taches = service.readAll().stream()
                .filter(t -> t.getSprint() != null && t.getSprint().getIdSprint() == idSprint)
                .collect(Collectors.toList());
            
            Map<String, Long> parStatut = taches.stream()
                .collect(Collectors.groupingBy(Tache::getStatut, Collectors.counting()));
            
            long total = taches.size();
            long terminees = parStatut.getOrDefault("DEJA_FAITE", 0L);
            double progression = total > 0 ? (terminees * 100.0 / total) : 0;
            
            System.out.println("📈 Rapport Sprint #" + idSprint);
            System.out.println("─".repeat(50));
            System.out.println("Total tâches : " + total);
            System.out.println("Terminées : " + terminees);
            System.out.println("En cours : " + parStatut.getOrDefault("EN_COURS", 0L));
            System.out.println("Pas commencées : " + parStatut.getOrDefault("PAS_ENCORE_FAITE", 0L));
            System.out.printf("Progression : %.2f%%%n", progression);
            System.out.println("─".repeat(50));
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

### 3. Notification de tâches en retard

```java
import Entite.Tache;
import Service.ServiceTache;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationRetard {
    public static void verifierRetards() {
        ServiceTache service = new ServiceTache();
        Date aujourdhui = new Date(System.currentTimeMillis());
        
        try {
            List<Tache> tachesEnRetard = service.readAll().stream()
                .filter(t -> !"DEJA_FAITE".equals(t.getStatut()))
                .filter(t -> t.getDateLimite() != null && t.getDateLimite().before(aujourdhui))
                .collect(Collectors.toList());
            
            if (!tachesEnRetard.isEmpty()) {
                System.out.println("⚠️  ALERTE : " + tachesEnRetard.size() + " tâche(s) en retard !");
                System.out.println("─".repeat(80));
                
                for (Tache t : tachesEnRetard) {
                    long joursRetard = (aujourdhui.getTime() - t.getDateLimite().getTime()) / (1000 * 60 * 60 * 24);
                    System.out.printf("• %s - En retard de %d jour(s) - Assignée à : %s%n",
                        t.getName(),
                        joursRetard,
                        t.getAffecte() != null ? t.getAffecte().getNom() : "Non assignée"
                    );
                }
                
                System.out.println("─".repeat(80));
            } else {
                System.out.println("✅ Aucune tâche en retard !");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
```

---

## 📝 Conclusion

Ces exemples couvrent les cas d'usage les plus courants du module de gestion de tâches. Vous pouvez les adapter selon vos besoins spécifiques.

**Ressources supplémentaires** :
- Documentation JavaFX : https://openjfx.io/
- JDBC Tutorial : https://docs.oracle.com/javase/tutorial/jdbc/
- Java Streams : https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
