# 📋 Documentation du Module de Gestion de Tâches - PlanUp

## 📑 Table des Matières
1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Base de données](#base-de-données)
4. [Backend (Java)](#backend-java)
5. [Frontend (JavaFX)](#frontend-javafx)
6. [Flux de données](#flux-de-données)
7. [Fonctionnalités par rôle](#fonctionnalités-par-rôle)

---

## 🎯 Vue d'ensemble

Le module de gestion de tâches permet de créer, modifier, supprimer et suivre des tâches dans le cadre de projets organisés en sprints. Il s'intègre dans l'application **PlanUp** et gère les permissions selon les rôles utilisateurs.

### Objectifs principaux :
- ✅ Créer et gérer des tâches
- ✅ Assigner des tâches aux développeurs/intégrateurs
- ✅ Suivre le statut des tâches (PAS_ENCORE_FAITE, EN_COURS, DEJA_FAITE)
- ✅ Organiser les tâches par sprints
- ✅ Gérer les permissions selon les rôles

---

## 🏗️ Architecture

L'application suit une architecture **MVC (Model-View-Controller)** en 3 couches :

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (JavaFX)                        │
│  TaskView.fxml + TaskController.java                        │
│  - Interface utilisateur                                    │
│  - Gestion des événements                                   │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  BACKEND (Services)                         │
│  ServiceTache.java                                          │
│  - Logique métier                                           │
│  - Opérations CRUD                                          │
│  - Validation                                               │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  MODÈLE (Entités)                           │
│  Tache.java                                                 │
│  - Représentation des données                               │
│  - Getters/Setters                                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              BASE DE DONNÉES (MySQL)                        │
│  Table: tache                                               │
│  - Stockage persistant                                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 💾 Base de données

### Table `tache`

```sql
CREATE TABLE `tache` (
  `id_tache` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `description` text DEFAULT NULL,
  `date_limite` date DEFAULT NULL,
  `duree` int(11) DEFAULT NULL,              -- Durée en jours
  `priorite` int(11) DEFAULT NULL,           -- 1 (haute) à 5 (basse)
  `estimation` int(11) DEFAULT NULL,         -- Estimation en heures
  `date_affectation` date DEFAULT NULL,
  `statut` varchar(50) DEFAULT 'PAS_ENCORE_FAITE',
  `id_sprint` int(11) DEFAULT NULL,
  `cin_affecte` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_tache`),
  FOREIGN KEY (`id_sprint`) REFERENCES `sprint`(`id_sprint`) ON DELETE SET NULL,
  FOREIGN KEY (`cin_affecte`) REFERENCES `utilisateur`(`cin`) ON DELETE SET NULL
);
```

### Relations :
- **tache ➔ sprint** : Une tâche appartient à un sprint (optionnel)
- **tache ➔ utilisateur** : Une tâche peut être assignée à un utilisateur (optionnel)

### Statuts possibles :
- `PAS_ENCORE_FAITE` : Tâche créée mais non démarrée
- `EN_COURS` : Tâche en cours de réalisation
- `DEJA_FAITE` : Tâche terminée

---

## ⚙️ Backend (Java)

### 1. Entité : `Tache.java`

**Emplacement** : `src/main/java/Entite/Tache.java`

**Rôle** : Représente une tâche avec tous ses attributs.

```java
public class Tache {
    private int idTache;
    private String name;
    private String description;
    private Date dateLimite;
    private int duree;
    private int priorite;
    private int estimation;
    private Date dateAffectation;
    private String statut;
    private Sprint sprint;           // Relation avec Sprint
    private Utilisateur affecte;     // Relation avec Utilisateur
    
    // Constructeurs, getters, setters, toString()...
}
```

**Points clés** :
- Contient des objets `Sprint` et `Utilisateur` pour les relations
- Le statut par défaut est `"PAS_ENCORE_FAITE"`
- Les dates utilisent `java.sql.Date`

---

### 2. Service : `ServiceTache.java`

**Emplacement** : `src/main/java/Service/ServiceTache.java`

**Rôle** : Gère toutes les opérations CRUD et la logique métier.

#### Méthodes principales :

| Méthode | Description | Paramètres | Retour |
|---------|-------------|------------|--------|
| `ajouter(Tache t)` | Ajoute une nouvelle tâche | Tache | boolean |
| `modifier(Tache t)` | Modifie une tâche existante | Tache | boolean |
| `supprimer(Tache t)` | Supprime une tâche | Tache | boolean |
| `findbyId(int id)` | Trouve une tâche par ID | int | Tache |
| `readAll()` | Récupère toutes les tâches | - | List<Tache> |
| `findByAssignee(int cin)` | Trouve les tâches d'un utilisateur | int | List<Tache> |
| `updateStatus(int idTache, String newStatus)` | Met à jour le statut | int, String | boolean |
| `assignToUser(int idTache, int cin)` | Assigne une tâche à un utilisateur | int, int | boolean |

#### Exemple d'implémentation - `ajouter()` :

```java
public boolean ajouter(Tache t) throws SQLException {
    String query = "INSERT INTO tache (name, description, date_limite, duree, " +
                   "priorite, estimation, date_affectation, statut, id_sprint, " +
                   "cin_affecte) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection c = DataSource.getInstance().getCon();
         PreparedStatement ps = c.prepareStatement(query)) {
        
        ps.setString(1, t.getName());
        ps.setString(2, t.getDescription());
        ps.setDate(3, t.getDateLimite());
        ps.setInt(4, t.getDuree());
        ps.setInt(5, t.getPriorite());
        ps.setInt(6, t.getEstimation());
        ps.setDate(7, t.getDateAffectation());
        ps.setString(8, (t.getStatut() == null) ? "PAS_ENCORE_FAITE" : t.getStatut());
        
        // Gestion des valeurs NULL pour les clés étrangères
        if (t.getSprint() != null)
            ps.setInt(9, t.getSprint().getIdSprint());
        else
            ps.setNull(9, Types.INTEGER);
            
        if (t.getAffecte() != null)
            ps.setInt(10, t.getAffecte().getCin());
        else
            ps.setNull(10, Types.INTEGER);
        
        return ps.executeUpdate() > 0;
    }
}
```

**Points clés** :
- Utilise `PreparedStatement` pour éviter les injections SQL
- Gère les valeurs NULL pour les relations optionnelles
- Les requêtes utilisent des **LEFT JOIN** pour récupérer les données liées
- La méthode `mapResultSetToTache()` convertit les résultats SQL en objets Java

---

## 🖥️ Frontend (JavaFX)

### 1. Vue : `TaskView.fxml`

**Emplacement** : `src/main/resources/view/TaskView.fxml`

**Structure** :

```
┌─────────────────────────────────────────────────────────┐
│  Gestion des Tâches                                     │
├──────────────────┬──────────────────────────────────────┤
│  FORMULAIRE      │  TABLEAU DES TÂCHES                  │
│  (Admin/Manager) │                                      │
│                  │  ┌────┬──────┬────────┬──────────┐  │
│  Nom: [____]     │  │ ID │ Nom  │ Statut │ Assigné  │  │
│  Description:    │  ├────┼──────┼────────┼──────────┤  │
│  [________]      │  │ 1  │ Task │ [▼]    │ Mohamed  │  │
│                  │  │ 2  │ Bug  │ [▼]    │ Sana     │  │
│  Date: [📅]      │  └────┴──────┴────────┴──────────┘  │
│  Priorité: [__]  │                                      │
│  Estimation: [_] │                                      │
│                  │                                      │
│  Sprint: [▼]     │                                      │
│  Assigné: [▼]    │                                      │
│                  │                                      │
│  [Ajouter] [Modifier]                                   │
│  [Supprimer] [Vider]                                    │
└──────────────────┴──────────────────────────────────────┘
```

**Composants principaux** :

| Composant | fx:id | Type | Description |
|-----------|-------|------|-------------|
| Table | `taskTable` | TableView | Affiche toutes les tâches |
| Colonne ID | `colId` | TableColumn | ID de la tâche |
| Colonne Nom | `colName` | TableColumn | Nom de la tâche |
| Colonne Statut | `colStatus` | TableColumn | ComboBox pour changer le statut |
| Colonne Assigné | `colAssignee` | TableColumn | Nom de la personne assignée |
| Champ Nom | `nameField` | TextField | Saisie du nom |
| Champ Description | `descArea` | TextArea | Saisie de la description |
| Sélecteur Date | `deadlinePicker` | DatePicker | Date limite |
| Champ Priorité | `priorityField` | TextField | Priorité (1-5) |
| Champ Estimation | `estimationField` | TextField | Estimation en heures |
| Liste Sprint | `sprintCombo` | ComboBox | Sélection du sprint |
| Liste Assigné | `assigneeCombo` | ComboBox | Sélection de l'utilisateur |

---

### 2. Contrôleur : `TaskController.java`

**Emplacement** : `src/main/java/Controller/TaskController.java`

#### Méthodes principales :

##### `initialize()`
- Configure les colonnes du tableau
- Ajoute un listener pour la sélection de tâches
- Charge les données initiales

```java
@FXML
public void initialize() {
    colId.setCellValueFactory(new PropertyValueFactory<>("idTache"));
    colName.setCellValueFactory(new PropertyValueFactory<>("name"));
    setupStatusColumn();
    
    taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal != null) {
            populateForm(newVal);
        }
    });
    
    loadData();
}
```

##### `setRoleAndUser(String role, Utilisateur user)`
- Configure les permissions selon le rôle
- Affiche/masque les éléments selon les droits

```java
public void setRoleAndUser(String role, Utilisateur user) {
    this.userRole = role;
    this.currentUser = user;
    
    boolean canCrud = "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role);
    boolean isDevOrInt = "DEVELOPPEUR".equalsIgnoreCase(role) || "INTEGRATEUR".equalsIgnoreCase(role);
    
    // Afficher le formulaire uniquement pour Admin/Manager
    if (formContainer != null) {
        formContainer.setVisible(canCrud);
        formContainer.setManaged(canCrud);
    }
    
    // Bouton "Assigner" uniquement pour Manager
    if (btnAssign != null) {
        btnAssign.setVisible("MANAGER".equalsIgnoreCase(role));
    }
    
    loadData();
}
```

##### `setupStatusColumn()`
- Crée une colonne avec des ComboBox pour changer le statut
- Met à jour automatiquement la base de données lors du changement

```java
private void setupStatusColumn() {
    colStatus.setCellValueFactory(new PropertyValueFactory<>("statut"));
    colStatus.setCellFactory(column -> new TableCell<Tache, String>() {
        private final ComboBox<String> comboBox = new ComboBox<>(
            FXCollections.observableArrayList("PAS_ENCORE_FAITE", "EN_COURS", "DEJA_FAITE")
        );
        
        {
            comboBox.setOnAction(event -> {
                Tache task = getTableView().getItems().get(getIndex());
                String newStatus = comboBox.getValue();
                if (task != null && newStatus != null && !newStatus.equals(task.getStatut())) {
                    try {
                        if (serviceTache.updateStatus(task.getIdTache(), newStatus)) {
                            task.setStatut(newStatus);
                            System.out.println("Statut mis à jour pour la tâche " + task.getIdTache());
                        }
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Mise à jour échouée", e.getMessage());
                    }
                }
            });
        }
        
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                comboBox.setValue(item);
                boolean canEdit = "DEVELOPPEUR".equalsIgnoreCase(userRole)
                        || "INTEGRATEUR".equalsIgnoreCase(userRole)
                        || "MANAGER".equalsIgnoreCase(userRole)
                        || "ADMIN".equalsIgnoreCase(userRole);
                comboBox.setDisable(!canEdit);
                setGraphic(comboBox);
            }
        }
    });
}
```

##### `loadData()`
- Charge les tâches selon le rôle de l'utilisateur
- Les Dev/Int voient uniquement leurs tâches
- Les Admin/Manager voient toutes les tâches

```java
private void loadData() {
    try {
        taskList.clear();
        if (currentUser != null && 
            ("DEVELOPPEUR".equalsIgnoreCase(userRole) || "INTEGRATEUR".equalsIgnoreCase(userRole))) {
            // Développeurs et intégrateurs voient uniquement leurs tâches
            taskList.addAll(serviceTache.findByAssignee(currentUser.getCin()));
        } else {
            // Admin et Manager voient toutes les tâches
            taskList.addAll(serviceTache.readAll());
        }
        taskTable.setItems(taskList);
        
        // Charger les listes pour les formulaires (Admin/Manager uniquement)
        if ("MANAGER".equalsIgnoreCase(userRole) || "ADMIN".equalsIgnoreCase(userRole)) {
            List<Utilisateur> users = serviceUtilisateur.readAll().stream()
                .filter(u -> "DEVELOPPEUR".equalsIgnoreCase(u.getRole()) 
                          || "INTEGRATEUR".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());
            assigneeCombo.setItems(FXCollections.observableArrayList(users));
            
            sprintCombo.setItems(FXCollections.observableArrayList(serviceSprint.readAll()));
        }
    } catch (SQLException e) {
        System.err.println("Error loading tasks: " + e.getMessage());
    }
}
```

##### `handleAdd()`
- Crée une nouvelle tâche à partir du formulaire

```java
@FXML
private void handleAdd() {
    try {
        Tache t = new Tache();
        t.setName(nameField.getText());
        t.setDescription(descArea.getText());
        if (deadlinePicker.getValue() != null)
            t.setDateLimite(Date.valueOf(deadlinePicker.getValue()));
        t.setPriorite(priorityField.getText().isEmpty() ? 0 : Integer.parseInt(priorityField.getText()));
        t.setEstimation(estimationField.getText().isEmpty() ? 0 : Integer.parseInt(estimationField.getText()));
        t.setStatut("PAS_ENCORE_FAITE");
        t.setSprint(sprintCombo.getValue());
        t.setAffecte(assigneeCombo.getValue());
        if (t.getAffecte() != null)
            t.setDateAffectation(new Date(System.currentTimeMillis()));
        
        if (serviceTache.ajouter(t)) {
            loadData();
            handleClear();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Tâche ajoutée", "La tâche a été créée.");
        }
    } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout échoué", e.getMessage());
    }
}
```

##### `handleUpdate()`
- Modifie la tâche sélectionnée

##### `handleDelete()`
- Supprime la tâche sélectionnée

##### `handleAssign()`
- Assigne une tâche à un utilisateur (Manager uniquement)

```java
@FXML
private void handleAssign() {
    Tache selected = taskTable.getSelectionModel().getSelectedItem();
    Utilisateur user = assigneeCombo.getValue();
    if (selected == null || user == null)
        return;
    
    try {
        if (serviceTache.assignToUser(selected.getIdTache(), user.getCin())) {
            loadData();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Tâche assignée", 
                     "Tâche assignée à " + user.getNom());
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Erreur", "Assignation échouée", e.getMessage());
    }
}
```

---

## 🔄 Flux de données

### 1. Création d'une tâche (Admin/Manager)

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐
│ Frontend │────▶│  Controller  │────▶│   Service    │────▶│    DB    │
│  (FXML)  │     │ handleAdd()  │     │  ajouter()   │     │  INSERT  │
└──────────┘     └──────────────┘     └──────────────┘     └──────────┘
     ▲                                                            │
     │                                                            │
     └────────────────────────────────────────────────────────────┘
                         loadData() - Rafraîchir
```

### 2. Changement de statut (Dev/Int/Manager/Admin)

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐
│ ComboBox │────▶│  Controller  │────▶│   Service    │────▶│    DB    │
│  onAction│     │setupStatus() │     │updateStatus()│     │  UPDATE  │
└──────────┘     └──────────────┘     └──────────────┘     └──────────┘
```

### 3. Affichage des tâches

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐
│ Frontend │◀────│  Controller  │◀────│   Service    │◀────│    DB    │
│TableView │     │  loadData()  │     │  readAll()   │     │  SELECT  │
└──────────┘     └──────────────┘     │findByAssignee│     └──────────┘
                                      └──────────────┘
```

---

## 👥 Fonctionnalités par rôle

### 🔴 ADMIN
- ✅ Voir toutes les tâches
- ✅ Créer des tâches
- ✅ Modifier des tâches
- ✅ Supprimer des tâches
- ✅ Changer le statut des tâches
- ✅ Assigner des tâches

### 🟠 MANAGER
- ✅ Voir toutes les tâches
- ✅ Créer des tâches
- ✅ Modifier des tâches
- ✅ Supprimer des tâches
- ✅ Changer le statut des tâches
- ✅ **Assigner des tâches aux développeurs/intégrateurs**

### 🟢 DEVELOPPEUR / INTEGRATEUR
- ✅ Voir **uniquement leurs tâches assignées**
- ✅ Changer le statut de leurs tâches
- ❌ Pas d'accès au formulaire de création/modification
- ❌ Ne peuvent pas supprimer de tâches

### 🔵 EMPLOYE / RH
- ❌ Pas d'accès au module de tâches (selon la configuration actuelle)

---

## 🔐 Sécurité et Permissions

### Contrôle d'accès dans le contrôleur :

```java
// Affichage du formulaire
boolean canCrud = "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role);
formContainer.setVisible(canCrud);

// Bouton d'assignation
btnAssign.setVisible("MANAGER".equalsIgnoreCase(role));

// Modification du statut
boolean canEdit = "DEVELOPPEUR".equalsIgnoreCase(userRole)
        || "INTEGRATEUR".equalsIgnoreCase(userRole)
        || "MANAGER".equalsIgnoreCase(userRole)
        || "ADMIN".equalsIgnoreCase(userRole);
comboBox.setDisable(!canEdit);

// Filtrage des données
if ("DEVELOPPEUR".equalsIgnoreCase(userRole) || "INTEGRATEUR".equalsIgnoreCase(userRole)) {
    taskList.addAll(serviceTache.findByAssignee(currentUser.getCin()));
} else {
    taskList.addAll(serviceTache.readAll());
}
```

---

## 📊 Schéma de flux complet

```
┌─────────────────────────────────────────────────────────────────┐
│                         UTILISATEUR                             │
└────────────┬────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    LOGIN (LoginController)                      │
│  - Authentification                                             │
│  - Récupération du rôle                                         │
└────────────┬────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   DASHBOARD (selon rôle)                        │
│  - DashboardAdmin.fxml                                          │
│  - DashboardManager.fxml                                        │
│  - DashboardDeveloppeur.fxml                                    │
└────────────┬────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    TaskView.fxml                                │
│  ┌──────────────────┬───────────────────────────────────────┐   │
│  │   FORMULAIRE     │      TABLEAU DES TÂCHES              │   │
│  │  (Admin/Manager) │                                       │   │
│  │                  │  - Affichage filtré selon rôle        │   │
│  │  - Nom           │  - ComboBox pour statut               │   │
│  │  - Description   │  - Sélection pour édition             │   │
│  │  - Date limite   │                                       │   │
│  │  - Priorité      │                                       │   │
│  │  - Estimation    │                                       │   │
│  │  - Sprint        │                                       │   │
│  │  - Assigné       │                                       │   │
│  │                  │                                       │   │
│  │  [Ajouter]       │                                       │   │
│  │  [Modifier]      │                                       │   │
│  │  [Supprimer]     │                                       │   │
│  │  [Assigner]      │                                       │   │
│  └──────────────────┴───────────────────────────────────────┘   │
└────────────┬────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    TaskController.java                          │
│  - initialize()                                                 │
│  - setRoleAndUser()                                             │
│  - loadData()                                                   │
│  - handleAdd/Update/Delete/Assign()                             │
│  - setupStatusColumn()                                          │
└────────────┬────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    ServiceTache.java                            │
│  - ajouter(Tache)                                               │
│  - modifier(Tache)                                              │
│  - supprimer(Tache)                                             │
│  - findbyId(int)                                                │
│  - readAll()                                                    │
│  - findByAssignee(int)                                          │
│  - updateStatus(int, String)                                    │
│  - assignToUser(int, int)                                       │
└────────────┬────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    DataSource (Connexion DB)                    │
└────────────┬────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    MySQL Database                               │
│  - Table: tache                                                 │
│  - Table: utilisateur                                           │
│  - Table: sprint                                                │
│  - Table: project                                               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🚀 Utilisation pratique

### Scénario 1 : Manager crée une tâche

1. Le Manager se connecte
2. Accède au module "Gestion des Tâches"
3. Remplit le formulaire :
   - Nom : "Implémenter login"
   - Description : "Création du module d'authentification"
   - Date limite : 2026-02-10
   - Priorité : 1
   - Estimation : 8 heures
   - Sprint : Sprint 1
   - Assigné : Mohamed (Développeur)
4. Clique sur "Ajouter"
5. La tâche apparaît dans le tableau avec le statut "PAS_ENCORE_FAITE"

### Scénario 2 : Développeur met à jour le statut

1. Le Développeur se connecte
2. Accède au module "Gestion des Tâches"
3. Voit uniquement ses tâches assignées
4. Sélectionne "Implémenter login"
5. Change le statut dans le ComboBox : "PAS_ENCORE_FAITE" → "EN_COURS"
6. Le statut est mis à jour automatiquement dans la base de données

### Scénario 3 : Manager assigne une tâche

1. Le Manager sélectionne une tâche non assignée
2. Choisit un développeur dans la liste "Assigné à"
3. Clique sur "Assigner"
4. La date d'affectation est automatiquement définie
5. Le développeur verra cette tâche dans sa liste

---

## 📝 Points importants

### ✅ Bonnes pratiques implémentées :
- **PreparedStatement** : Protection contre les injections SQL
- **Try-with-resources** : Gestion automatique des ressources
- **Séparation des responsabilités** : MVC respecté
- **Gestion des NULL** : Vérifications pour les relations optionnelles
- **Permissions basées sur les rôles** : Sécurité intégrée

### ⚠️ Points d'attention :
- Le champ `statut` dans la table SQL n'existe pas encore (ajouté uniquement dans le code Java)
- Il faut ajouter la colonne `statut` à la table `tache` :
  ```sql
  ALTER TABLE tache ADD COLUMN statut VARCHAR(50) DEFAULT 'PAS_ENCORE_FAITE';
  ```

### 🔧 Améliorations possibles :
- Ajouter une validation des champs (priorité entre 1-5, etc.)
- Implémenter un système de notifications
- Ajouter des filtres de recherche
- Créer un historique des modifications
- Ajouter des graphiques de suivi (Kanban board)

---

## 📚 Fichiers concernés

| Fichier | Chemin | Rôle |
|---------|--------|------|
| Tache.java | `src/main/java/Entite/Tache.java` | Modèle de données |
| ServiceTache.java | `src/main/java/Service/ServiceTache.java` | Logique métier |
| TaskController.java | `src/main/java/Controller/TaskController.java` | Contrôleur |
| TaskView.fxml | `src/main/resources/view/TaskView.fxml` | Interface utilisateur |
| esprit1alinfo1.sql | `esprit1alinfo1 (1).sql` | Schéma de base de données |

---

## 🎓 Conclusion

Le module de gestion de tâches est un système complet qui permet :
- Une gestion efficace des tâches dans un contexte Agile (sprints)
- Un contrôle d'accès basé sur les rôles
- Une interface intuitive avec mise à jour en temps réel
- Une architecture propre et maintenable

Il s'intègre parfaitement dans l'écosystème PlanUp et peut être étendu facilement pour ajouter de nouvelles fonctionnalités.
