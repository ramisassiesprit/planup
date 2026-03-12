package Service;

import Entite.Utilisateur;

public interface IAuthentificationService {
    Utilisateur login(String email, String motDePasse);
    Utilisateur loginByCin(int cin, String motDePasse);
    boolean emailExists(String email);
    boolean cinExists(int cin);
    boolean register(Utilisateur utilisateur);
    boolean changePassword(String email, String ancienMotDePasse, String nouveauMotDePasse);
    boolean hasRole(Utilisateur utilisateur, String role);
}
