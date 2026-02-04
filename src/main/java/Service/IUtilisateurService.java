package Service;

import Entite.Utilisateur;
import java.util.List;

public interface IUtilisateurService {
    boolean ajouter(Utilisateur u);
    List<Utilisateur> afficher();
    boolean modifier(Utilisateur u);
    boolean supprimer(int cin);
    Utilisateur findByCin(int cin);
    Utilisateur findByEmail(String email);
}
