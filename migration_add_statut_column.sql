-- Migration pour ajouter la colonne 'statut' à la table 'tache'
-- Cette colonne est nécessaire pour le bon fonctionnement du module de gestion de tâches

-- Ajouter la colonne statut avec une valeur par défaut
ALTER TABLE `tache` 
ADD COLUMN `statut` VARCHAR(50) DEFAULT 'PAS_ENCORE_FAITE' 
AFTER `date_affectation`;

-- Mettre à jour les tâches existantes avec le statut par défaut
UPDATE `tache` 
SET `statut` = 'PAS_ENCORE_FAITE' 
WHERE `statut` IS NULL;

-- Vérification : afficher toutes les tâches avec leur statut
SELECT id_tache, name, statut FROM tache;
