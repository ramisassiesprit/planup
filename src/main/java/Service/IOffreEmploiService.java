package Service;

import Entite.OffreEmploi;
import java.util.List;

public interface IOffreEmploiService {
    boolean ajouter(OffreEmploi o);
    List<OffreEmploi> afficher();
    boolean modifier(OffreEmploi o);
    boolean supprimer(int idOffre);
    OffreEmploi findById(int idOffre);
}
