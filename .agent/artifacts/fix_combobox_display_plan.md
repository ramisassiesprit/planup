# Plan de correction - Affichage des ComboBox dans TaskController

## 🎯 Objectif
Corriger l'affichage des listes déroulantes (ComboBox) pour les sprints et les utilisateurs dans le module de gestion de tâches.

## 🔍 Problème identifié

Lorsqu'un Manager/Admin tente d'assigner une tâche, les ComboBox pour :
- **Sprint** : N'affiche pas correctement les noms des sprints
- **Utilisateur** : N'affiche pas correctement les noms des utilisateurs

### Cause racine
Les ComboBox JavaFX utilisent par défaut la méthode `toString()` des objets pour l'affichage. Les classes `Sprint` et `Utilisateur` ont des méthodes `toString()` qui retournent un format technique (ex: "Sprint{idSprint=1, name='Sprint 1', project=Projet Alpha}") au lieu d'un format lisible pour l'utilisateur.

## 📋 Solution proposée

### Approche 1 : Modifier les méthodes toString() (Simple mais impacte tout le code)
- ❌ Pas recommandé car `toString()` est utilisé pour le débogage

### Approche 2 : Utiliser des StringConverter personnalisés (Recommandé)
- ✅ Permet un affichage personnalisé sans modifier les entités
- ✅ Maintient la séparation des responsabilités
- ✅ Facile à maintenir

## 🛠️ Tâches à réaliser

### Tâche 1 : Ajouter des StringConverter pour Sprint
**Fichier** : `TaskController.java`
**Ligne** : Après l'initialisation des ComboBox (ligne ~172)

**Action** :
```java
// Configurer l'affichage du ComboBox Sprint
sprintCombo.setConverter(new StringConverter<Sprint>() {
    @Override
    public String toString(Sprint sprint) {
        if (sprint == null) return "";
        return sprint.getName() + " (" + 
               (sprint.getProject() != null ? sprint.getProject().getName() : "Aucun projet") + ")";
    }
    
    @Override
    public Sprint fromString(String string) {
        return null; // Pas nécessaire pour notre cas
    }
});
```

### Tâche 2 : Ajouter des StringConverter pour Utilisateur
**Fichier** : `TaskController.java`
**Ligne** : Après l'initialisation des ComboBox (ligne ~170)

**Action** :
```java
// Configurer l'affichage du ComboBox Utilisateur
assigneeCombo.setConverter(new StringConverter<Utilisateur>() {
    @Override
    public String toString(Utilisateur user) {
        if (user == null) return "";
        return user.getNom() + " " + user.getPrenom() + " (" + user.getRole() + ")";
    }
    
    @Override
    public Utilisateur fromString(String string) {
        return null; // Pas nécessaire pour notre cas
    }
});
```

### Tâche 3 : Ajouter l'import nécessaire
**Fichier** : `TaskController.java`
**Ligne** : Dans la section des imports (ligne ~1-20)

**Action** :
```java
import javafx.util.StringConverter;
```

### Tâche 4 : Tester le fonctionnement
**Actions** :
1. Compiler le projet
2. Lancer l'application
3. Se connecter en tant que Manager/Admin
4. Accéder au module de gestion de tâches
5. Vérifier que les ComboBox affichent correctement :
   - Les sprints avec leur projet associé
   - Les utilisateurs avec leur nom complet et rôle

## ✅ Critères de succès

- [ ] Le ComboBox Sprint affiche : "Sprint 1 (Projet Alpha)"
- [ ] Le ComboBox Utilisateur affiche : "Ben Ali Mohamed (DEVELOPPEUR)"
- [ ] La sélection d'un sprint fonctionne correctement
- [ ] La sélection d'un utilisateur fonctionne correctement
- [ ] L'assignation d'une tâche fonctionne sans erreur
- [ ] Les données sont correctement sauvegardées en base de données

## 🔄 Améliorations futures (optionnel)

1. **Ajouter un placeholder** dans les ComboBox vides
2. **Trier les listes** alphabétiquement
3. **Ajouter des icônes** pour différencier les rôles
4. **Filtrage dynamique** dans les ComboBox

## 📝 Notes techniques

- Les `StringConverter` sont appliqués uniquement à l'affichage, pas aux données
- L'objet complet (Sprint/Utilisateur) est toujours stocké dans le ComboBox
- La méthode `fromString()` peut retourner `null` car nous n'avons pas besoin de conversion inverse
