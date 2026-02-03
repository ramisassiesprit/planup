package Entite;

import java.sql.Date;

public class OffreEmploi {
    private int idOffre;
    private String titre;
    private String description;
    private String profilRecherche;
    private String typeContrat;
    private double salaire;
    private String localisation;
    private Date datePublication;
    private String statut;
    private Utilisateur rh;

    public OffreEmploi() {
    }

    public OffreEmploi(int idOffre, String titre, String description, String profilRecherche, String typeContrat,
            double salaire, String localisation, Date datePublication, String statut, Utilisateur rh) {
        this.idOffre = idOffre;
        this.titre = titre;
        this.description = description;
        this.profilRecherche = profilRecherche;
        this.typeContrat = typeContrat;
        this.salaire = salaire;
        this.localisation = localisation;
        this.datePublication = datePublication;
        this.statut = statut;
        this.rh = rh;
    }

    public OffreEmploi(String titre, String description, String profilRecherche, String typeContrat, double salaire,
            String localisation, Date datePublication, String statut, Utilisateur rh) {
        this.titre = titre;
        this.description = description;
        this.profilRecherche = profilRecherche;
        this.typeContrat = typeContrat;
        this.salaire = salaire;
        this.localisation = localisation;
        this.datePublication = datePublication;
        this.statut = statut;
        this.rh = rh;
    }

    public int getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilRecherche() {
        return profilRecherche;
    }

    public void setProfilRecherche(String profilRecherche) {
        this.profilRecherche = profilRecherche;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Utilisateur getRh() {
        return rh;
    }

    public void setRh(Utilisateur rh) {
        this.rh = rh;
    }

    @Override
    public String toString() {
        return "OffreEmploi{" +
                "idOffre=" + idOffre +
                ", titre='" + titre + '\'' +
                ", salaire=" + salaire +
                ", datePublication=" + datePublication +
                ", rh=" + (rh != null ? rh.getNom() : "null") +
                '}';
    }
}
