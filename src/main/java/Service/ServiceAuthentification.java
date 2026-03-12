package Service;

import Entite.Utilisateur;
import Utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service d'authentification pour gérer la connexion des utilisateurs
 */
public class ServiceAuthentification implements IAuthentificationService {

    private Connection con = DataSource.getInstance().getCon();
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    /**
     * Authentifie un utilisateur avec son email et mot de passe
     * 
     * @param email      Email de l'utilisateur
     * @param motDePasse Mot de passe de l'utilisateur
     * @return L'utilisateur si authentification réussie, null sinon
     */
    @Override
    public Utilisateur login(String email, String motDePasse) {
        String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, motDePasse);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur user = new Utilisateur(
                            rs.getInt("cin"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("num_tel"),
                            rs.getString("role"));
                    System.out.println("Connexion réussie pour: " + user.getEmail());
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'authentification: " + e.getMessage());
        }
        System.out.println("Échec de connexion: email ou mot de passe incorrect");
        return null;
    }

    /**
     * Authentifie un utilisateur avec son CIN et mot de passe
     * 
     * @param cin        CIN de l'utilisateur
     * @param motDePasse Mot de passe de l'utilisateur
     * @return L'utilisateur si authentification réussie, null sinon
     */
    @Override
    public Utilisateur loginByCin(int cin, String motDePasse) {
        String sql = "SELECT * FROM utilisateur WHERE cin = ? AND mot_de_passe = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cin);
            ps.setString(2, motDePasse);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur user = new Utilisateur(
                            rs.getInt("cin"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("mot_de_passe"),
                            rs.getString("num_tel"),
                            rs.getString("role"));
                    System.out.println("Connexion réussie pour: " + user.getEmail());
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'authentification: " + e.getMessage());
        }
        System.out.println("Échec de connexion: CIN ou mot de passe incorrect");
        return null;
    }

    /**
     * Vérifie si un email existe déjà dans la base de données
     * 
     * @param email Email à vérifier
     * @return true si l'email existe, false sinon
     */
    @Override
    public boolean emailExists(String email) {
        return serviceUtilisateur.findByEmail(email) != null;
    }

    /**
     * Vérifie si un CIN existe déjà dans la base de données
     * 
     * @param cin CIN à vérifier
     * @return true si le CIN existe, false sinon
     */
    @Override
    public boolean cinExists(int cin) {
        return serviceUtilisateur.findByCin(cin) != null;
    }

    /**
     * Inscrit un nouvel utilisateur
     * 
     * @param utilisateur L'utilisateur à inscrire
     * @return true si l'inscription réussit, false sinon
     */
    @Override
    public boolean register(Utilisateur utilisateur) {
        // Vérifier si l'email existe déjà
        if (emailExists(utilisateur.getEmail())) {
            System.out.println("Erreur: Cet email est déjà utilisé");
            return false;
        }

        // Vérifier si le CIN existe déjà
        if (cinExists(utilisateur.getCin())) {
            System.out.println("Erreur: Ce CIN est déjà enregistré");
            return false;
        }

        // Ajouter l'utilisateur
        try {
            if (serviceUtilisateur.ajouter(utilisateur)) {
                System.out.println("Inscription réussie pour: " + utilisateur.getEmail());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'inscription (SQL): " + e.getMessage());
        }

        System.out.println("Erreur lors de l'inscription");
        return false;
    }

    /**
     * Change le mot de passe d'un utilisateur
     * 
     * @param email             Email de l'utilisateur
     * @param ancienMotDePasse  Ancien mot de passe
     * @param nouveauMotDePasse Nouveau mot de passe
     * @return true si le changement réussit, false sinon
     */
    @Override
    public boolean changePassword(String email, String ancienMotDePasse, String nouveauMotDePasse) {
        // Vérifier l'ancien mot de passe
        Utilisateur user = login(email, ancienMotDePasse);
        if (user == null) {
            System.out.println("Erreur: Ancien mot de passe incorrect");
            return false;
        }

        // Mettre à jour le mot de passe
        user.setMotDePasse(nouveauMotDePasse);
        try {
            if (serviceUtilisateur.modifier(user)) {
                System.out.println("Mot de passe changé avec succès");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du changement de mot de passe (SQL): " + e.getMessage());
        }

        System.out.println("Erreur lors du changement de mot de passe");
        return false;
    }

    /**
     * Vérifie si un utilisateur a un rôle spécifique
     * 
     * @param utilisateur L'utilisateur à vérifier
     * @param role        Le rôle à vérifier
     * @return true si l'utilisateur a ce rôle, false sinon
     */
    @Override
    public boolean hasRole(Utilisateur utilisateur, String role) {
        return utilisateur != null && utilisateur.getRole().equalsIgnoreCase(role);
    }
}