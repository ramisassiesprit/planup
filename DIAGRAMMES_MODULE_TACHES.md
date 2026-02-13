# 🎨 Diagrammes et Schémas - Module de Gestion de Tâches

## 📊 Diagramme de Classes

```
┌─────────────────────────────────────────────────────────────────┐
│                            Tache                                │
├─────────────────────────────────────────────────────────────────┤
│ - idTache: int                                                  │
│ - name: String                                                  │
│ - description: String                                           │
│ - dateLimite: Date                                              │
│ - duree: int                                                    │
│ - priorite: int                                                 │
│ - estimation: int                                               │
│ - dateAffectation: Date                                         │
│ - statut: String                                                │
│ - sprint: Sprint                                                │
│ - affecte: Utilisateur                                          │
├─────────────────────────────────────────────────────────────────┤
│ + Tache()                                                       │
│ + Tache(...)                                                    │
│ + getters/setters                                               │
│ + toString(): String                                            │
└────────────┬────────────────────────────────┬───────────────────┘
             │                                │
             │ 0..1                           │ 0..1
             │                                │
             ▼                                ▼
┌─────────────────────┐            ┌─────────────────────┐
│      Sprint         │            │    Utilisateur      │
├─────────────────────┤            ├─────────────────────┤
│ - idSprint: int     │            │ - cin: int          │
│ - name: String      │            │ - nom: String       │
│ - idProject: int    │            │ - prenom: String    │
├─────────────────────┤            │ - email: String     │
│ + getters/setters   │            │ - role: String      │
└─────────────────────┘            ├─────────────────────┤
                                   │ + getters/setters   │
                                   └─────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      ServiceTache                               │
├─────────────────────────────────────────────────────────────────┤
│ + ajouter(Tache): boolean                                       │
│ + modifier(Tache): boolean                                      │
│ + supprimer(Tache): boolean                                     │
│ + findbyId(int): Tache                                          │
│ + readAll(): List<Tache>                                        │
│ + findByAssignee(int): List<Tache>                              │
│ + updateStatus(int, String): boolean                            │
│ + assignToUser(int, int): boolean                               │
│ - mapResultSetToTache(ResultSet): Tache                         │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    TaskController                               │
├─────────────────────────────────────────────────────────────────┤
│ - taskTable: TableView<Tache>                                   │
│ - nameField: TextField                                          │
│ - descArea: TextArea                                            │
│ - deadlinePicker: DatePicker                                    │
│ - sprintCombo: ComboBox<Sprint>                                 │
│ - assigneeCombo: ComboBox<Utilisateur>                          │
│ - serviceTache: ServiceTache                                    │
│ - userRole: String                                              │
│ - currentUser: Utilisateur                                      │
├─────────────────────────────────────────────────────────────────┤
│ + initialize()                                                  │
│ + setRoleAndUser(String, Utilisateur)                           │
│ - setupStatusColumn()                                           │
│ - loadData()                                                    │
│ - populateForm(Tache)                                           │
│ + handleAdd()                                                   │
│ + handleUpdate()                                                │
│ + handleDelete()                                                │
│ + handleAssign()                                                │
│ + handleClear()                                                 │
│ - showAlert(...)                                                │
└─────────────────────────────────────────────────────────────────┘
```

## 🔄 Diagramme de Séquence - Création d'une tâche

```
Manager    TaskView    TaskController    ServiceTache    Database
  │            │              │                │             │
  │ Remplit    │              │                │             │
  │ formulaire │              │                │             │
  │────────────▶              │                │             │
  │            │              │                │             │
  │ Clique     │              │                │             │
  │ "Ajouter"  │              │                │             │
  │────────────▶              │                │             │
  │            │              │                │             │
  │            │ handleAdd()  │                │             │
  │            │──────────────▶                │             │
  │            │              │                │             │
  │            │              │ Créer objet    │             │
  │            │              │ Tache          │             │
  │            │              │────────┐       │             │
  │            │              │        │       │             │
  │            │              │◀───────┘       │             │
  │            │              │                │             │
  │            │              │ ajouter(t)     │             │
  │            │              │────────────────▶             │
  │            │              │                │             │
  │            │              │                │ INSERT INTO │
  │            │              │                │ tache       │
  │            │              │                │─────────────▶
  │            │              │                │             │
  │            │              │                │ Succès      │
  │            │              │                │◀─────────────
  │            │              │                │             │
  │            │              │ true           │             │
  │            │              │◀────────────────             │
  │            │              │                │             │
  │            │              │ loadData()     │             │
  │            │              │────────┐       │             │
  │            │              │        │       │             │
  │            │              │◀───────┘       │             │
  │            │              │                │             │
  │            │              │ readAll()      │             │
  │            │              │────────────────▶             │
  │            │              │                │             │
  │            │              │                │ SELECT *    │
  │            │              │                │─────────────▶
  │            │              │                │             │
  │            │              │                │ ResultSet   │
  │            │              │                │◀─────────────
  │            │              │                │             │
  │            │              │ List<Tache>    │             │
  │            │              │◀────────────────             │
  │            │              │                │             │
  │            │ Rafraîchir   │                │             │
  │            │ TableView    │                │             │
  │            │◀──────────────                │             │
  │            │              │                │             │
  │ Afficher   │              │                │             │
  │ Alert      │              │                │             │
  │◀────────────              │                │             │
  │            │              │                │             │
```

## 🔄 Diagramme de Séquence - Changement de statut

```
Développeur  ComboBox    TaskController    ServiceTache    Database
    │            │              │                │             │
    │ Sélectionne│              │                │             │
    │ nouveau    │              │                │             │
    │ statut     │              │                │             │
    │────────────▶              │                │             │
    │            │              │                │             │
    │            │ onAction()   │                │             │
    │            │──────────────▶                │             │
    │            │              │                │             │
    │            │              │ Récupérer      │             │
    │            │              │ tâche          │             │
    │            │              │────────┐       │             │
    │            │              │        │       │             │
    │            │              │◀───────┘       │             │
    │            │              │                │             │
    │            │              │ updateStatus() │             │
    │            │              │────────────────▶             │
    │            │              │                │             │
    │            │              │                │ UPDATE tache│
    │            │              │                │ SET statut  │
    │            │              │                │─────────────▶
    │            │              │                │             │
    │            │              │                │ Succès      │
    │            │              │                │◀─────────────
    │            │              │                │             │
    │            │              │ true           │             │
    │            │              │◀────────────────             │
    │            │              │                │             │
    │            │              │ Mettre à jour  │             │
    │            │              │ objet local    │             │
    │            │              │────────┐       │             │
    │            │              │        │       │             │
    │            │              │◀───────┘       │             │
    │            │              │                │             │
    │            │ Confirmation │                │             │
    │            │◀──────────────                │             │
    │            │              │                │             │
    │ Voir       │              │                │             │
    │ changement │              │                │             │
    │◀────────────              │                │             │
    │            │              │                │             │
```

## 🗄️ Schéma de Base de Données

```
┌─────────────────────────────────────────────────────────────────┐
│                          utilisateur                            │
├─────────────────────────────────────────────────────────────────┤
│ PK  cin: int                                                    │
│     nom: varchar(100)                                           │
│     prenom: varchar(100)                                        │
│     email: varchar(150) UNIQUE                                  │
│     mot_de_passe: varchar(255)                                  │
│     num_tel: varchar(50)                                        │
│     role: varchar(50)                                           │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ 1
                         │
                         │ 0..*
                         │
┌────────────────────────┴────────────────────────────────────────┐
│                            tache                                │
├─────────────────────────────────────────────────────────────────┤
│ PK  id_tache: int AUTO_INCREMENT                                │
│     name: varchar(200)                                          │
│     description: text                                           │
│     date_limite: date                                           │
│     duree: int                                                  │
│     priorite: int                                               │
│     estimation: int                                             │
│     date_affectation: date                                      │
│     statut: varchar(50) DEFAULT 'PAS_ENCORE_FAITE'              │
│ FK  id_sprint: int                                              │
│ FK  cin_affecte: int                                            │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ 0..*
                         │
                         │ 1
                         │
┌────────────────────────┴────────────────────────────────────────┐
│                           sprint                                │
├─────────────────────────────────────────────────────────────────┤
│ PK  id_sprint: int AUTO_INCREMENT                               │
│     name: varchar(150)                                          │
│ FK  id_project: int                                             │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ 0..*
                         │
                         │ 1
                         │
┌────────────────────────┴────────────────────────────────────────┐
│                          project                                │
├─────────────────────────────────────────────────────────────────┤
│ PK  id_project: int AUTO_INCREMENT                              │
│     name: varchar(150)                                          │
│     type: varchar(100)                                          │
└─────────────────────────────────────────────────────────────────┘
```

## 🎭 Diagramme des Cas d'Utilisation

```
                    ┌─────────────────────────────────────┐
                    │  Système de Gestion de Tâches      │
                    └─────────────────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
        │                           │                           │
        ▼                           ▼                           ▼
┌───────────────┐          ┌───────────────┐          ┌───────────────┐
│     ADMIN     │          │    MANAGER    │          │ DEVELOPPEUR/  │
│               │          │               │          │  INTEGRATEUR  │
└───────┬───────┘          └───────┬───────┘          └───────┬───────┘
        │                          │                          │
        │                          │                          │
        │ ┌────────────────────────┼──────────────────────┐   │
        │ │                        │                      │   │
        ▼ ▼                        ▼                      ▼   ▼
    ┌─────────────┐          ┌─────────────┐        ┌─────────────┐
    │   Créer     │          │  Assigner   │        │   Voir mes  │
    │   tâche     │          │   tâche     │        │   tâches    │
    └─────────────┘          └─────────────┘        └─────────────┘
        │                          │                      │
        ▼                          │                      ▼
    ┌─────────────┐                │                ┌─────────────┐
    │  Modifier   │                │                │  Changer    │
    │   tâche     │                │                │   statut    │
    └─────────────┘                │                └─────────────┘
        │                          │
        ▼                          │
    ┌─────────────┐                │
    │  Supprimer  │                │
    │   tâche     │                │
    └─────────────┘                │
        │                          │
        ▼                          ▼
    ┌─────────────────────────────────┐
    │   Voir toutes les tâches        │
    └─────────────────────────────────┘
```

## 🔐 Matrice des Permissions

```
┌──────────────────┬────────┬─────────┬──────────────┬──────┐
│   Fonctionnalité │ ADMIN  │ MANAGER │ DEV/INT      │  RH  │
├──────────────────┼────────┼─────────┼──────────────┼──────┤
│ Voir toutes      │   ✅   │   ✅    │      ❌      │  ❌  │
│ les tâches       │        │         │              │      │
├──────────────────┼────────┼─────────┼──────────────┼──────┤
│ Voir mes         │   ✅   │   ✅    │      ✅      │  ❌  │
│ tâches           │        │         │              │      │
├──────────────────┼────────┼─────────┼──────────────┼──────┤
│ Créer            │   ✅   │   ✅    │      ❌      │  ❌  │
│ tâche            │        │         │              │      │
├──────────────────┼────────┼─────────┼──────────────┼──────┤
│ Modifier         │   ✅   │   ✅    │      ❌      │  ❌  │
│ tâche            │        │         │              │      │
├──────────────────┼────────┼─────────┼──────────────┼──────┤
│ Supprimer        │   ✅   │   ✅    │      ❌      │  ❌  │
│ tâche            │        │         │              │      │
├──────────────────┼────────┼─────────┼──────────────┼──────┤
│ Assigner         │   ✅   │   ✅    │      ❌      │  ❌  │
│ tâche            │        │         │              │      │
├──────────────────┼────────┼─────────┼──────────────┼──────┤
│ Changer          │   ✅   │   ✅    │      ✅      │  ❌  │
│ statut           │        │         │              │      │
└──────────────────┴────────┴─────────┴──────────────┴──────┘
```

## 📈 Diagramme d'État - Cycle de vie d'une tâche

```
                    ┌─────────────────────┐
                    │   Tâche créée       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │  PAS_ENCORE_FAITE   │
                    └──────────┬──────────┘
                               │
                               │ Développeur commence
                               │ le travail
                               ▼
                    ┌─────────────────────┐
                    │     EN_COURS        │◀─────┐
                    └──────────┬──────────┘      │
                               │                 │
                               │ Peut revenir    │
                               │ en arrière      │
                               │                 │
                               │ Tâche terminée  │
                               ▼                 │
                    ┌─────────────────────┐      │
                    │    DEJA_FAITE       │──────┘
                    └─────────────────────┘
                               │
                               │ Optionnel
                               ▼
                    ┌─────────────────────┐
                    │  Tâche supprimée    │
                    └─────────────────────┘
```

## 🌊 Flux de Navigation

```
┌─────────────────┐
│  Page Login     │
└────────┬────────┘
         │
         │ Authentification réussie
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│              Sélection du Dashboard                     │
├─────────────────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐ │
│  │  ADMIN   │  │ MANAGER  │  │  DEV/INT │  │   RH   │ │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬───┘ │
└───────┼─────────────┼─────────────┼─────────────┼─────┘
        │             │             │             │
        │             │             │             │
        ▼             ▼             ▼             ▼
┌──────────────────────────────────────────────────────────┐
│                  Menu Navigation                         │
├──────────────────────────────────────────────────────────┤
│  • Projets                                               │
│  • Sprints                                               │
│  • Tâches  ◀──── Nous sommes ici                        │
│  • Utilisateurs                                          │
│  • Congés                                                │
│  • Offres d'emploi                                       │
└────────────────────┬─────────────────────────────────────┘
                     │
                     │ Clic sur "Tâches"
                     │
                     ▼
┌──────────────────────────────────────────────────────────┐
│              TaskView.fxml                               │
├──────────────────────────────────────────────────────────┤
│  ┌────────────────┬──────────────────────────────────┐  │
│  │  Formulaire    │    Tableau des tâches            │  │
│  │  (si autorisé) │                                  │  │
│  └────────────────┴──────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

## 🎯 Workflow - Gestion complète d'une tâche

```
┌─────────────────────────────────────────────────────────────┐
│                    CYCLE COMPLET                            │
└─────────────────────────────────────────────────────────────┘

1. CRÉATION (Manager/Admin)
   │
   ├─▶ Remplir formulaire
   │   ├─ Nom
   │   ├─ Description
   │   ├─ Date limite
   │   ├─ Priorité
   │   ├─ Estimation
   │   └─ Sprint
   │
   ├─▶ Cliquer "Ajouter"
   │
   └─▶ Tâche créée avec statut "PAS_ENCORE_FAITE"

2. ASSIGNATION (Manager)
   │
   ├─▶ Sélectionner tâche
   │
   ├─▶ Choisir développeur/intégrateur
   │
   ├─▶ Cliquer "Assigner"
   │
   └─▶ Date d'affectation enregistrée

3. TRAVAIL (Développeur/Intégrateur)
   │
   ├─▶ Voir la tâche dans "Mes tâches"
   │
   ├─▶ Changer statut → "EN_COURS"
   │
   ├─▶ Travailler sur la tâche
   │
   └─▶ Changer statut → "DEJA_FAITE"

4. SUIVI (Manager/Admin)
   │
   ├─▶ Voir toutes les tâches
   │
   ├─▶ Filtrer par statut/sprint/assigné
   │
   └─▶ Analyser la progression

5. MODIFICATION (Manager/Admin)
   │
   ├─▶ Sélectionner tâche
   │
   ├─▶ Modifier les détails
   │
   └─▶ Cliquer "Modifier"

6. SUPPRESSION (Manager/Admin)
   │
   ├─▶ Sélectionner tâche
   │
   └─▶ Cliquer "Supprimer"
```

---

**Note** : Ces diagrammes sont en format ASCII pour une meilleure compatibilité. Pour une version graphique professionnelle, vous pouvez utiliser des outils comme :
- **PlantUML** pour les diagrammes UML
- **Draw.io** pour les schémas d'architecture
- **Lucidchart** pour les flux de processus
- **dbdiagram.io** pour les schémas de base de données
