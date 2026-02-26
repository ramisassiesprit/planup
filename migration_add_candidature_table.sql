-- Migration: Add candidature table for tracking job applications
-- This table tracks when a candidate applies for a job offer and their response status

CREATE TABLE IF NOT EXISTS `candidature` (
  `id_candidature` int(11) NOT NULL AUTO_INCREMENT,
  `cin_candidat` int(11) NOT NULL,
  `id_offre` int(11) NOT NULL,
  `statut` varchar(50) DEFAULT 'PENDING',
  `date_postulation` date DEFAULT CURDATE(),
  `lettre_motivation` text DEFAULT NULL,
  PRIMARY KEY (`id_candidature`),
  KEY `fk_candidature_candidat` (`cin_candidat`),
  KEY `fk_candidature_offre` (`id_offre`),
  UNIQUE KEY `unique_candidature` (`cin_candidat`, `id_offre`),
  CONSTRAINT `fk_candidature_candidat` FOREIGN KEY (`cin_candidat`) REFERENCES `utilisateur` (`cin`) ON DELETE CASCADE,
  CONSTRAINT `fk_candidature_offre` FOREIGN KEY (`id_offre`) REFERENCES `offre_emploi` (`id_offre`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Add some test candidatures
INSERT INTO `candidature` (`cin_candidat`, `id_offre`, `statut`, `date_postulation`, `lettre_motivation`) VALUES
(12345678, 1, 'PENDING', CURDATE(), 'Je suis très intéressé par ce poste.');
