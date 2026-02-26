package Service;

import Entite.Conge;
import Entite.Utilisateur;
import Utils.DataSource;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CongeService implements ICongeService {

    @Override
    public boolean demanderConge(Conge c) {
        if (c == null || c.getUtilisateur() == null) {
            System.out.println("Données de congé invalides.");
            return false;
        }
        // Vérifier que l'utilisateur existe
        ServiceUtilisateur us = new ServiceUtilisateur();
        Utilisateur u = us.findByCin(c.getUtilisateur().getCin());
        if (u == null) {
            System.out.println("Utilisateur introuvable.");
            return false;
        }
        String sql = "INSERT INTO conge (cin, type, date_debut, date_fin, nbr_jours, justificatif, statut, solde_conge, conge_solde) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = DataSource.getInstance().getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, u.getCin());
            ps.setString(2, c.getType());
            ps.setDate(3, c.getDateDebut());
            ps.setDate(4, c.getDateFin());
            ps.setInt(5, c.getNbrJours());
            ps.setString(6, c.getJustificatif());
            ps.setString(7, c.getStatut() != null ? c.getStatut() : "EN_ATTENTE");
            ps.setObject(8, c.getSoldeConge() == 0 ? null : c.getSoldeConge());
            ps.setInt(9, c.getCongeSolde());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur SQL demanderConge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Conge getById(int id) {
        String sql = "SELECT c.id_conge, c.type, c.date_debut, c.date_fin, c.nbr_jours, c.justificatif, c.statut, c.solde_conge, c.conge_solde, u.cin as u_cin, u.nom, u.prenom, u.email, u.mot_de_passe, u.num_tel, u.role FROM conge c LEFT JOIN utilisateur u ON c.cin = u.cin WHERE c.id_conge = ?";
        try (Connection con = DataSource.getInstance().getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = new Utilisateur();
                    u.setCin(rs.getInt("u_cin"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setEmail(rs.getString("email"));
                    u.setMotDePasse(rs.getString("mot_de_passe"));
                    u.setNumTel(rs.getString("num_tel"));
                    u.setRole(rs.getString("role"));

                    Conge c = new Conge();
                    c.setIdConge(rs.getInt("id_conge"));
                    c.setUtilisateur(u);
                    c.setType(rs.getString("type"));
                    c.setDateDebut(rs.getDate("date_debut"));
                    c.setDateFin(rs.getDate("date_fin"));
                    c.setNbrJours(rs.getInt("nbr_jours"));
                    c.setJustificatif(rs.getString("justificatif"));
                    c.setStatut(rs.getString("statut"));
                    c.setSoldeConge(rs.getInt("solde_conge"));
                    c.setCongeSolde(rs.getInt("conge_solde"));
                    return c;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL getById Conge: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Conge> listerConges() {
        List<Conge> list = new ArrayList<>();
        String sql = "SELECT c.id_conge, c.type, c.date_debut, c.date_fin, c.nbr_jours, c.justificatif, c.statut, c.solde_conge, c.conge_solde, u.cin as u_cin, u.nom, u.prenom, u.email, u.mot_de_passe, u.num_tel, u.role FROM conge c LEFT JOIN utilisateur u ON c.cin = u.cin";
        try (Connection con = DataSource.getInstance().getCon(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setCin(rs.getInt("u_cin"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setMotDePasse(rs.getString("mot_de_passe"));
                u.setNumTel(rs.getString("num_tel"));
                u.setRole(rs.getString("role"));

                Conge c = new Conge();
                c.setIdConge(rs.getInt("id_conge"));
                c.setUtilisateur(u);
                c.setType(rs.getString("type"));
                c.setDateDebut(rs.getDate("date_debut"));
                c.setDateFin(rs.getDate("date_fin"));
                c.setNbrJours(rs.getInt("nbr_jours"));
                c.setJustificatif(rs.getString("justificatif"));
                c.setStatut(rs.getString("statut"));
                c.setSoldeConge(rs.getInt("solde_conge"));
                c.setCongeSolde(rs.getInt("conge_solde"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL listerConges: " + e.getMessage());
        }
        return list;
    }

    // Compatibilité: ancienne méthode utilisée dans Main et ailleurs
    public List<Conge> consulterConges() {
        return listerConges();
    }

    @Override
    public boolean accepterConge(int congeId, int rhCin, String commentaire) {
        ServiceUtilisateur us = new ServiceUtilisateur();
        Utilisateur rh = us.findByCin(rhCin);
        if (rh == null || (!"RH".equalsIgnoreCase(rh.getRole()) && !"ADMIN".equalsIgnoreCase(rh.getRole()))) {
            System.out.println("Action réservée au RH ou ADMIN.");
            return false;
        }
        Conge c = getById(congeId);
        if (c == null) {
            System.out.println("Conge introuvable.");
            return false;
        }
        if (!"EN_ATTENTE".equalsIgnoreCase(c.getStatut())) {
            System.out.println("Le congé n'est pas en attente.");
            return false;
        }
        String sql = "UPDATE conge SET statut = ? WHERE id_conge = ? AND statut = 'EN_ATTENTE'";
        try (Connection con = DataSource.getInstance().getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "ACCEPTE");
            ps.setInt(2, congeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur SQL accepterConge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean refuserConge(int congeId, int rhCin, String commentaire) {
        ServiceUtilisateur us = new ServiceUtilisateur();
        Utilisateur rh = us.findByCin(rhCin);
        if (rh == null || (!"RH".equalsIgnoreCase(rh.getRole()) && !"ADMIN".equalsIgnoreCase(rh.getRole()))) {
            System.out.println("Action réservée au RH ou ADMIN.");
            return false;
        }
        Conge c = getById(congeId);
        if (c == null) {
            System.out.println("Conge introuvable.");
            return false;
        }
        if (!"EN_ATTENTE".equalsIgnoreCase(c.getStatut())) {
            System.out.println("Le congé n'est pas en attente.");
            return false;
        }
        String sql = "UPDATE conge SET statut = ? WHERE id_conge = ? AND statut = 'EN_ATTENTE'";
        try (Connection con = DataSource.getInstance().getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "REFUSE");
            ps.setInt(2, congeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur SQL refuserConge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean annulerConge(int congeId, int employeCin) {
        Conge c = getById(congeId);
        if (c == null) {
            System.out.println("Conge introuvable.");
            return false;
        }
        if (employeCin != -1 && c.getUtilisateur() != null && c.getUtilisateur().getCin() != employeCin) {
            System.out.println("Vous n'êtes pas le propriétaire de ce congé.");
            return false;
        }
        if (!"EN_ATTENTE".equalsIgnoreCase(c.getStatut())) {
            System.out.println("Seuls les congés en attente peuvent être annulés.");
            return false;
        }
        String sql = "DELETE FROM conge WHERE id_conge = ?";
        try (Connection con = DataSource.getInstance().getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, congeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur SQL annulerConge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int calculateSoldeRestant(int cin) {
        // Supposons que l'employé gagne 1.75 jours par mois
        // Pour cet exemple, on considère qu'il a travaillé 12 mois par défaut (ou on pourrait calculer depuis une date fixe)
        double joursAcquisParMois = 1.75;
        int nbMoisTravailles = 12; // Valeur par défaut car date_embauche absente
        
        int totalAcquis = (int) (nbMoisTravailles * joursAcquisParMois);
        
        // Calculer les jours déjà pris (Acceptés seulement)
        int joursPris = 0;
        String sql = "SELECT SUM(nbr_jours) FROM conge WHERE cin = ? AND statut = 'ACCEPTE'";
        try (Connection con = DataSource.getInstance().getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    joursPris = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur calculateSoldeRestant: " + e.getMessage());
        }
        
        return totalAcquis - joursPris;
    }
}
