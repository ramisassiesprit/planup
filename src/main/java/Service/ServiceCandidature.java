package Service;

import Entite.Candidature;
import Entite.OffreEmploi;
import Entite.Utilisateur;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCandidature {
    private Connection connect = DataSource.getInstance().getCon();
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private ServiceOffreEmploi serviceOffreEmploi = new ServiceOffreEmploi();

    public boolean ajouter(Candidature candidature) {
        String req = "INSERT INTO candidature (cin_candidat, id_offre, statut, date_postulation, lettre_motivation) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connect.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, candidature.getCandidat().getCin());
            pst.setInt(2, candidature.getOffre().getIdOffre());
            pst.setString(3, candidature.getStatut());
            pst.setDate(4, candidature.getDatePostulation());
            pst.setString(5, candidature.getLettreMotivation());

            int res = pst.executeUpdate();
            if (res > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    candidature.setIdCandidature(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Candidature> afficher() {
        List<Candidature> candidatures = new ArrayList<>();
        String req = "SELECT * FROM candidature";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                candidatures.add(mapResultSetToCandidature(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidatures;
    }

    public List<Candidature> afficherByCandidatCin(int cin) {
        List<Candidature> candidatures = new ArrayList<>();
        String req = "SELECT * FROM candidature WHERE cin_candidat = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setInt(1, cin);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                candidatures.add(mapResultSetToCandidature(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidatures;
    }

    public List<Candidature> afficherByOffreId(int idOffre) {
        List<Candidature> candidatures = new ArrayList<>();
        String req = "SELECT * FROM candidature WHERE id_offre = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setInt(1, idOffre);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                candidatures.add(mapResultSetToCandidature(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidatures;
    }

    public boolean updateStatut(int idCandidature, String newStatut) {
        String req = "UPDATE candidature SET statut = ? WHERE id_candidature = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setString(1, newStatut);
            pst.setInt(2, idCandidature);
            int res = pst.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Candidature getCandidatureByCanidatAndOffre(int cinCandidat, int idOffre) {
        String req = "SELECT * FROM candidature WHERE cin_candidat = ? AND id_offre = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setInt(1, cinCandidat);
            pst.setInt(2, idOffre);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToCandidature(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean supprimer(int idCandidature) {
        String req = "DELETE FROM candidature WHERE id_candidature = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setInt(1, idCandidature);
            int res = pst.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Candidature mapResultSetToCandidature(ResultSet rs) throws SQLException {
        int idCandidature = rs.getInt("id_candidature");
        int cinCandidat = rs.getInt("cin_candidat");
        int idOffre = rs.getInt("id_offre");
        String statut = rs.getString("statut");
        Date datePostulation = rs.getDate("date_postulation");
        String lettreMotivation = rs.getString("lettre_motivation");

        Utilisateur candidat = serviceUtilisateur.findByCin(cinCandidat);
        OffreEmploi offre = serviceOffreEmploi.findById(idOffre);

        return new Candidature(idCandidature, candidat, offre, statut, datePostulation, lettreMotivation);
    }
}
