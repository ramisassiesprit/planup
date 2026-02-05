package Service;

import Entite.Utilisateur;
import Utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceUtilisateur {
    private Connection connect = DataSource.getInstance().getCon();

    public Utilisateur authenticate(String email, String password) throws SQLException {
        String req = "SELECT * FROM `utilisateur` WHERE `email` = ? AND `mot_de_passe` = ?";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setString(1, email);
        pst.setString(2, password);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return mapResultSetToUtilisateur(rs);
        }
        return null; // Connexion échouée
    }

    public Utilisateur findByEmail(String email) {
        String req = "SELECT * FROM `utilisateur` WHERE `email` = ?";
        try {PreparedStatement pst = connect.prepareStatement(req);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Utilisateur findByCin(int cin) {
        String req = "SELECT * FROM `utilisateur` WHERE `cin` = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setInt(1, cin);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean ajouter(Utilisateur utilisateur) {
        String req = "INSERT INTO `utilisateur` (`cin`, `nom`, `prenom`, `email`, `mot_de_passe`, `num_tel`, `role`) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setInt(1, utilisateur.getCin());
            pst.setString(2, utilisateur.getNom());
            pst.setString(3, utilisateur.getPrenom());
            pst.setString(4, utilisateur.getEmail());
            pst.setString(5, utilisateur.getMotDePasse());
            pst.setString(6, utilisateur.getNumTel());
            pst.setString(7, utilisateur.getRole());
            int res = pst.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifier(Utilisateur utilisateur) {
        String req = "UPDATE `utilisateur` SET `nom` = ?, `prenom` = ?, `email` = ?, `mot_de_passe` = ?, `num_tel` = ?, `role` = ? WHERE `cin` = ?";
        try {
            PreparedStatement pst = connect.prepareStatement(req);
            pst.setString(1, utilisateur.getNom());
            pst.setString(2, utilisateur.getPrenom());
            pst.setString(3, utilisateur.getEmail());
            pst.setString(4, utilisateur.getMotDePasse());
            pst.setString(5, utilisateur.getNumTel());
            pst.setString(6, utilisateur.getRole());
            pst.setInt(7, utilisateur.getCin());
            int res = pst.executeUpdate();
            return res > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        return new Utilisateur(
                rs.getInt("cin"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getString("num_tel"),
                rs.getString("role")
        );
    }
}
