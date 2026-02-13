-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 13, 2026 at 09:40 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `esprit1alinfo1`
--

-- --------------------------------------------------------

--
-- Table structure for table `conge`
--

CREATE TABLE `conge` (
  `id_conge` int(11) NOT NULL,
  `cin` int(11) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `date_debut` date DEFAULT NULL,
  `date_fin` date DEFAULT NULL,
  `nbr_jours` int(11) DEFAULT NULL,
  `justificatif` varchar(255) DEFAULT NULL,
  `statut` varchar(50) DEFAULT 'EN_ATTENTE',
  `solde_conge` int(11) DEFAULT NULL,
  `conge_solde` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `conge`
--

INSERT INTO `conge` (`id_conge`, `cin`, `type`, `date_debut`, `date_fin`, `nbr_jours`, `justificatif`, `statut`, `solde_conge`, `conge_solde`) VALUES
(1, 12345678, 'ANNUEL', '2026-02-01', '2026-02-05', 5, 'justif.pdf', 'ACCEPTE', 18, NULL),
(2, 13034567, 'SANS_SOLDE', '2026-02-12', '2026-02-15', 3, 'djerba', 'ACCEPTE', NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `offre_emploi`
--

CREATE TABLE `offre_emploi` (
  `id_offre` int(11) NOT NULL,
  `titre` varchar(150) NOT NULL,
  `description` text NOT NULL,
  `profil_recherche` text DEFAULT NULL,
  `type_contrat` varchar(50) DEFAULT NULL,
  `salaire` decimal(10,2) DEFAULT NULL,
  `localisation` varchar(100) DEFAULT NULL,
  `date_publication` date DEFAULT curdate(),
  `statut` varchar(30) DEFAULT 'OUVERTE',
  `cin_rh` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `offre_emploi`
--

INSERT INTO `offre_emploi` (`id_offre`, `titre`, `description`, `profil_recherche`, `type_contrat`, `salaire`, `localisation`, `date_publication`, `statut`, `cin_rh`) VALUES
(1, 'Dev Java', 'Participation au dev du projet ERP', 'Java, MySQL, UML', 'CDI', 2500.00, 'Tunis', '2026-01-22', 'OUVERTE', 87654321),
(2, 'Dev Java', 'Participation au dev du projet ERP', 'Java, MySQL, UML', 'CDI', 2500.00, 'Tunis', '2026-02-12', 'OUVERTE', 11111111),
(3, 'titre', 'alternace w moto5lsch', 'dev', 'CDI', 150.00, 'hayzouhour', '2026-02-12', 'En cours', 11111111);

-- --------------------------------------------------------

--
-- Table structure for table `project`
--

CREATE TABLE `project` (
  `id_project` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `type` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `project`
--

INSERT INTO `project` (`id_project`, `name`, `type`) VALUES
(1, 'Projet Alpha', 'Web'),
(2, 'Projet Beta', 'Mobile'),
(3, 'Nouveau Projet de Test', 'Desktop'),
(4, 'facebook', 'social web');

-- --------------------------------------------------------

--
-- Table structure for table `sprint`
--

CREATE TABLE `sprint` (
  `id_sprint` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `id_project` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sprint`
--

INSERT INTO `sprint` (`id_sprint`, `name`, `id_project`) VALUES
(1, 'Sprint 1', 1),
(2, 'Sprint 2', 1),
(3, 'Sprint de Test', 3),
(4, 'messagerie', 4),
(5, 'messagerie', 4);

-- --------------------------------------------------------

--
-- Table structure for table `tache`
--

CREATE TABLE `tache` (
  `id_tache` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `description` text DEFAULT NULL,
  `date_limite` date DEFAULT NULL,
  `duree` int(11) DEFAULT NULL,
  `priorite` int(11) DEFAULT NULL,
  `estimation` int(11) DEFAULT NULL,
  `date_affectation` date DEFAULT NULL,
  `id_sprint` int(11) DEFAULT NULL,
  `cin_affecte` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tache`
--

INSERT INTO `tache` (`id_tache`, `name`, `description`, `date_limite`, `duree`, `priorite`, `estimation`, `date_affectation`, `id_sprint`, `cin_affecte`) VALUES
(1, 'Implémenter login', 'Création du module d\'authentification', '2026-02-10', 5, 1, 8, '2026-01-15', 1, 12345678);

-- --------------------------------------------------------

--
-- Table structure for table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `cin` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `mot_de_passe` varchar(255) DEFAULT NULL,
  `num_tel` varchar(50) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `utilisateur`
--

INSERT INTO `utilisateur` (`cin`, `nom`, `prenom`, `email`, `mot_de_passe`, `num_tel`, `role`) VALUES
(1447708, 'man', 'man', 'man', 'man', '88888888', 'MANAGER'),
(1777088, 'dev', 'dev', 'dev', 'dev', '99999907', 'DEVELOPPEUR'),
(11111111, 'rh', 'rh', 'rh', 'rh', '44444444', 'RH'),
(11111162, 'emp', 'emp', 'emp', 'emp', '07774419', 'EMPLOYE'),
(12345678, 'Ben Ali', 'Mohamed', 'mohamed.benali@example.com', 'password', '12345678', 'EMPLOYE'),
(13034567, 'brahmi', 'feres', 'feres', 'feres', '58804300', 'ADMIN'),
(66666666, 'int', 'int', 'int', 'int', '78945612', 'INTEGRATEUR'),
(87654321, 'Ben Amor', 'Sana', 'sana.benamor@example.com', 'adminpass', '98765432', 'RH'),
(880476100, 'ahmed', 'hammouda', 'benhammouda@emp.tn', 'desturbed', '21703500', 'DEVELOPPEUR');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `conge`
--
ALTER TABLE `conge`
  ADD PRIMARY KEY (`id_conge`),
  ADD KEY `fk_conge_utilisateur` (`cin`);

--
-- Indexes for table `offre_emploi`
--
ALTER TABLE `offre_emploi`
  ADD PRIMARY KEY (`id_offre`),
  ADD KEY `fk_offre_rh` (`cin_rh`);

--
-- Indexes for table `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`id_project`);

--
-- Indexes for table `sprint`
--
ALTER TABLE `sprint`
  ADD PRIMARY KEY (`id_sprint`),
  ADD KEY `fk_sprint_project` (`id_project`);

--
-- Indexes for table `tache`
--
ALTER TABLE `tache`
  ADD PRIMARY KEY (`id_tache`),
  ADD KEY `fk_tache_sprint` (`id_sprint`),
  ADD KEY `fk_tache_utilisateur` (`cin_affecte`);

--
-- Indexes for table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`cin`),
  ADD UNIQUE KEY `uniq_email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `conge`
--
ALTER TABLE `conge`
  MODIFY `id_conge` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `offre_emploi`
--
ALTER TABLE `offre_emploi`
  MODIFY `id_offre` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `project`
--
ALTER TABLE `project`
  MODIFY `id_project` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `sprint`
--
ALTER TABLE `sprint`
  MODIFY `id_sprint` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `tache`
--
ALTER TABLE `tache`
  MODIFY `id_tache` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `conge`
--
ALTER TABLE `conge`
  ADD CONSTRAINT `fk_conge_utilisateur` FOREIGN KEY (`cin`) REFERENCES `utilisateur` (`cin`) ON DELETE CASCADE;

--
-- Constraints for table `offre_emploi`
--
ALTER TABLE `offre_emploi`
  ADD CONSTRAINT `fk_offre_rh` FOREIGN KEY (`cin_rh`) REFERENCES `utilisateur` (`cin`) ON DELETE CASCADE;

--
-- Constraints for table `sprint`
--
ALTER TABLE `sprint`
  ADD CONSTRAINT `fk_sprint_project` FOREIGN KEY (`id_project`) REFERENCES `project` (`id_project`) ON DELETE CASCADE;

--
-- Constraints for table `tache`
--
ALTER TABLE `tache`
  ADD CONSTRAINT `fk_tache_sprint` FOREIGN KEY (`id_sprint`) REFERENCES `sprint` (`id_sprint`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_tache_utilisateur` FOREIGN KEY (`cin_affecte`) REFERENCES `utilisateur` (`cin`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
