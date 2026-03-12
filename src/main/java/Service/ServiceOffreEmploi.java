package Service;

import Entite.OffreEmploi;
import Entite.Utilisateur;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOffreEmploi {
    private Connection connect = DataSource.getInstance().getCon();
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    public boolean ajouter(OffreEmploi o) {
        String req = "INSERT INTO offre_emploi (titre, description, profil_recherche, type_contrat, salaire, localisation, date_publication, statut, cin_rh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connect.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, o.getTitre());
            pst.setString(2, o.getDescription());
            pst.setString(3, o.getProfilRecherche());
            pst.setString(4, o.getTypeContrat());
            pst.setDouble(5, o.getSalaire());
            pst.setString(6, o.getLocalisation());
            pst.setDate(7, o.getDatePublication());
            pst.setString(8, o.getStatut());
            if (o.getRh() != null) {
                pst.setInt(9, o.getRh().getCin());
            } else {
                pst.setNull(9, java.sql.Types.INTEGER);
            }

            int res = pst.executeUpdate();
            if (res > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    o.setIdOffre(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<OffreEmploi> afficher() {
        List<OffreEmploi> offres = new ArrayList<>();
        String req = "SELECT * FROM offre_emploi";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                offres.add(mapResultSetToOffreEmploi(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offres;
    }

    public boolean modifier(OffreEmploi o) {
        String req = "UPDATE offre_emploi SET titre = ?, description = ?, profil_recherche = ?, type_contrat = ?, salaire = ?, localisation = ?, date_publication = ?, statut = ?, cin_rh = ? WHERE id_offre = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setString(1, o.getTitre());
            pst.setString(2, o.getDescription());
            pst.setString(3, o.getProfilRecherche());
            pst.setString(4, o.getTypeContrat());
            pst.setDouble(5, o.getSalaire());
            pst.setString(6, o.getLocalisation());
            pst.setDate(7, o.getDatePublication());
            pst.setString(8, o.getStatut());
            if (o.getRh() != null) {
                pst.setInt(9, o.getRh().getCin());
            } else {
                pst.setNull(9, java.sql.Types.INTEGER);
            }
            pst.setInt(10, o.getIdOffre());

            int res = pst.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimer(int idOffre) {
        String req = "DELETE FROM offre_emploi WHERE id_offre = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setInt(1, idOffre);
            int res = pst.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public OffreEmploi findById(int idOffre) {
        String req = "SELECT * FROM offre_emploi WHERE id_offre = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setInt(1, idOffre);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToOffreEmploi(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OffreEmploi mapResultSetToOffreEmploi(ResultSet rs) throws SQLException {
        Utilisateur rh = null;
        int cinRh = rs.getInt("cin_rh");
        if (!rs.wasNull()) {
            rh = serviceUtilisateur.findByCin(cinRh);
        }
        return new OffreEmploi(
                rs.getInt("id_offre"),
                rs.getString("titre"),
                rs.getString("description"),
                rs.getString("profil_recherche"),
                rs.getString("type_contrat"),
                rs.getDouble("salaire"),
                rs.getString("localisation"),
                rs.getDate("date_publication"),
                rs.getString("statut"),
                rh
        );
    }
}
