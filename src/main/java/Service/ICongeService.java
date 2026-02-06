package Service;

import Entite.Conge;

import java.util.List;

public interface ICongeService {
    boolean demanderConge(Conge c);
    Conge getById(int id);
    List<Conge> listerConges();
    boolean accepterConge(int congeId, int rhCin, String commentaire);
    boolean refuserConge(int congeId, int rhCin, String commentaire);
    boolean annulerConge(int congeId, int employeCin);
}
