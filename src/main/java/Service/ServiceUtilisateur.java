package Service;

import Entite.Utilisateur;
import Utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur implements IUtilisateurService {
    private final Connection connect = DataSource.getInstance().getCon();

    public boolean ajouter(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (cin, nom, prenom, email, mot_de_passe, num_tel, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, u.getCin());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getMotDePasse());
            ps.setString(6, u.getNumTel());
            ps.setString(7, u.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur ajout utilisateur: " + e.getMessage());
        }
        return false;
    }

    public List<Utilisateur> afficher() {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";
        try (Statement st = connect.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setCin(rs.getInt("cin"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setEmail(rs.getString("email"));
                u.setMotDePasse(rs.getString("mot_de_passe"));
                u.setNumTel(rs.getString("num_tel"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erreur affichage utilisateur: " + e.getMessage());
        }
        return list;
    }

    public boolean modifier(Utilisateur u) {
        String sql = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, num_tel = ?, role = ? WHERE cin = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getMotDePasse());
            ps.setString(5, u.getNumTel());
            ps.setString(6, u.getRole());
            ps.setInt(7, u.getCin());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur modification utilisateur: " + e.getMessage());
        }
        return false;
    }

    public boolean supprimer(int cin) {
        String sql = "DELETE FROM utilisateur WHERE cin = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, cin);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur suppression utilisateur: " + e.getMessage());
        }
        return false;
    }

    public Utilisateur findByCin(int cin) {
        String sql = "SELECT * FROM utilisateur WHERE cin = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        } catch (SQLException e) {
            System.out.println("Erreur findByCin utilisateur: " + e.getMessage());
        }
        return null;
    }

    public Utilisateur findByEmail(String email) {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        } catch (SQLException e) {
            System.out.println("Erreur findByEmail utilisateur: " + e.getMessage());
        }
        return null;
    }

    public Utilisateur authenticate(String email, String motDePasse) throws SQLException {
        String req = "SELECT * FROM `utilisateur` WHERE `email` = ? AND `mot_de_passe` = ? LIMIT 1;";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setString(1, email);
        pst.setString(2, motDePasse);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
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
        return null;
    }
}
