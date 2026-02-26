# Implémentation du Rôle Candidat avec Gestion des Candidatures

## 📋 Résumé des Modifications

Cette implémentation ajoute un nouveau rôle **CANDIDAT** à l'application PlanUp, permettant aux candidats de:
- ✅ Se connecter avec leurs identifiants
- ✅ Parcourir les offres d'emploi disponibles
- ✅ Candidater aux offres avec une lettre de motivation
- ✅ Accepter ou refuser les offres après candidature
- ✅ Voir l'historique de leurs candidatures

Et permet aux responsables RH de:
- ✅ Voir toutes les candidatures pour chaque offre
- ✅ Visualiser le statut des candidatures (PENDING, ACCEPTED, DECLINED)
- ✅ Identifier les candidats qui ont accepté ou refusé les offres

---

## 📁 Fichiers Créés

### 1. **Entite/Candidature.java**
Nouvelle entité pour représenter une candidature avec:
- `idCandidature`: Identifiant unique
- `candidat`: Référence au candidat (Utilisateur)
- `offre`: Référence à l'offre d'emploi (OffreEmploi)
- `statut`: PENDING, ACCEPTED, ou DECLINED
- `datePostulation`: Date de la candidature
- `lettreMotivation`: Lettre de motivation du candidat

### 2. **Service/ServiceCandidature.java**
Service de gestion des candidatures avec méthodes:
- `ajouter()`: Créer une nouvelle candidature
- `afficher()`: Récupérer toutes les candidatures
- `afficherByCandidatCin()`: Candidatures d'un candidat spécifique
- `afficherByOffreId()`: Candidatures pour une offre spécifique
- `updateStatut()`: Mettre à jour le statut d'une candidature
- `getCandidatureByCanidatAndOffre()`: Vérifier si un candidat a déjà candidaté
- `supprimer()`: Supprimer une candidature

### 3. **Controller/DashboardCandidatController.java**
Contrôleur pour le tableau de bord candidat avec:
- Affichage de toutes les offres d'emploi disponibles
- Possibilité de consulter les détails de chaque offre
- Formulaire de candidature avec lettre de motivation
- Gestion des réponses (accepter/refuser) après candidature
- Onglet "Mes Candidatures" pour voir l'historique

### 4. **resources/view/DashboardCandidat.fxml**
Interface graphique pour le candidat avec:
- TabPane avec 2 onglets: "Parcourir les Offres" et "Mes Candidatures"
- Tableau des offres disponibles (titre, entreprise, salaire, type contrat, localisation)
- Panneau de détails de l'offre avec description
- Zone de saisie de la lettre de motivation
- Boutons: Candidater, Accepter, Refuser
- Tableau des candidatures avec statut

---

## 📊 Fichiers Modifiés

### 1. **Controller/LoginController.java**
✅ Ajout du support pour le rôle CANDIDAT:
```java
case "CANDIDAT":
    fxmlFile = "/view/DashboardCandidat.fxml";
    break;
```
✅ Instanciation du contrôleur approprié pour le candidat

### 2. **Controller/DashboardRHController.java**
✅ Ajout de ServiceCandidature
✅ Ajout de TableView pour afficher les candidatures
✅ Ajout de setupCandidaturesTable() pour configurer le tableau
✅ Ajout de loadCandidaturesForOffre() pour charger les candidatures
✅ Mise à jour de setupOffreTable() pour charger les candidatures au sélection
✅ Modification de handleClear() pour nettoyer aussi les candidatures
✅ Modification de handleDelete() pour supprimer aussi les candidatures associées

### 3. **resources/view/DashboardRH.fxml**
✅ Remplacement du simple TableView par un SplitPane:
  - Côté gauche: TableView des offres d'emploi
  - Côté droit: TableView des candidatures pour l'offre sélectionnée
✅ Ajout des colonnes pour afficher:
  - Nom du candidat
  - Email du candidat
  - Statut de la candidature
  - Date de postulation

---

## 🗄️ Migration Base de Données

### **migration_add_candidature_table.sql**
Crée la table `candidature` avec structure:

```sql
CREATE TABLE candidature (
  id_candidature INT AUTO_INCREMENT PRIMARY KEY,
  cin_candidat INT NOT NULL,
  id_offre INT NOT NULL,
  statut VARCHAR(50) DEFAULT 'PENDING', -- PENDING, ACCEPTED, DECLINED
  date_postulation DATE DEFAULT CURDATE(),
  lettre_motivation TEXT,
  UNIQUE KEY unique_candidature (cin_candidat, id_offre),
  FOREIGN KEY (cin_candidat) REFERENCES utilisateur(cin),
  FOREIGN KEY (id_offre) REFERENCES offre_emploi(id_offre)
);
```

---

## 🔄 Workflow Candidat

### Processus de Candidature:
1. **Connexion**: Candidat se connecte avec email/mot de passe
2. **Parcourir**: Voir toutes les offres disponibles dans le tableau
3. **Consulter**: Sélectionner une offre pour voir les détails
4. **Candidater**: Écrire une lettre de motivation et soumettre
5. **Gérer**: 
   - Voir le statut "PENDING" dans "Mes Candidatures"
   - Accepter ou refuser l'offre
   - Le statut devient "ACCEPTED" ou "DECLINED"

---

## 🔄 Workflow RH

### Suivi des Candidatures:
1. **Dashboard RH**: Accès au tableau des offres d'emploi
2. **Sélectionner une Offre**: Clique sur une offre dans le tableau
3. **Voir Candidatures**: Le tableau de droite affiche tous les candidats
4. **Filtrer par Statut**: 
   - PENDING: Candidatures en attente de réponse du candidat
   - ACCEPTED: Candidats ayant accepté l'offre
   - DECLINED: Candidats ayant refusé l'offre

---

## 👥 Types d'Utilisateurs et Rôles

L'application supporte maintenant 7 rôles:
1. **ADMIN**: Administrateur système
2. **MANAGER**: Chef de projet
3. **RH**: Responsable Ressources Humaines
4. **DEVELOPPEUR**: Développeur logiciel
5. **INTEGRATEUR**: Intégrateur système
6. **CANDIDAT**: Candidat externe (NOUVEAU)
7. (Autres rôles existants: EMPLOYE, etc.)

---

## 🔒 Sécurité et Contraintes

✅ Chaque candidature est unique (`cin_candidat`, `id_offre`)
✅ Impossible de candidater deux fois à la même offre
✅ Les candidatures sont supprimées si l'offer ou le candidat est supprimé
✅ Statuts validés: PENDING, ACCEPTED, DECLINED

---

## 📝 Étapes d'Installation

### 1. **Exécuter la migration SQL**:
```sql
-- Exécuter migration_add_candidature_table.sql
```

### 2. **Créer des candidats de test** (optionnel):
```sql
INSERT INTO utilisateur (cin, nom, prenom, email, mot_de_passe, num_tel, role)
VALUES 
(11111111, 'Dupont', 'Jean', 'jean.dupont@example.com', 'password', '12345678', 'CANDIDAT'),
(22222222, 'Martin', 'Sophie', 'sophie.martin@example.com', 'password', '87654321', 'CANDIDAT');
```

### 3. **Recompiler l'application**:
```bash
mvn clean compile
mvn clean package
```

### 4. **Lancer l'application**

---

## 🧪 Scénarios de Test

### Test 1: Candidat se connecte et candidate
1. Connexion: email: `jean.dupont@example.com`, password: `password`
2. Dashboard Candidat s'ouvre
3. Sélectionner une offre
4. Écrire une lettre de motivation
5. Cliquer "Candidater"
6. Vérifier le statut dans "Mes Candidatures"

### Test 2: Candidat accepte une offre
1. Dans "Mes Candidatures", une candidature en statut PENDING apparaît
2. Sélectionner l'offre dans le premier onglet
3. Cliquer "Accepter l'Offre"
4. Vérifier le statut devient "ACCEPTED"

### Test 3: RH voit les candidatures
1. Connexion RH
2. Dashboard RH s'ouvre
3. Sélectionner une offre dans le tableau gauche
4. Le tableau droit affiche tous les candidats
5. Vérifier les colonnes: Candidat, Email, Statut, Date Postulation

---

## 📚 Références des Entités

### Candidature
- Liée à: **Utilisateur** (candidat)
- Liée à: **OffreEmploi** (offre)

### Utilisateur
- Nouveau rôle accepté: **CANDIDAT**

### OffreEmploi
- Reste inchangé mais maintenant visible par les candidats

---

## ✨ Fonctionnalités Futures (Optionnel)

- 📧 Notification email au candidat lors d'acceptation/refus
- 📄 CV du candidat dans le formulaire de candidature
- ⭐ Évaluation des candidats par le RH
- 📊 Statistiques sur les offres (nombre de candidatures, taux d'acceptation)
- 🔍 Filtre avancé des candidatures par statut
- 📋 Export des candidatures en PDF/Excel

---

## 🐛 Troubleshooting

### Erreur: "DashboardCandidat.fxml not found"
→ Vérifier que le fichier est dans `src/main/resources/view/`

### Erreur: ServiceCandidature introuvable
→ Vérifier l'import dans DashboardRHController

### Candidatures n'apparaissent pas
→ Vérifier que la table `candidature` est créée dans la base de données
→ Vérifier les contraintes de clé étrangère

---

**Implémentation complétée avec succès! ✅**
