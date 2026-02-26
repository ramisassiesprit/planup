package Entite;

import java.sql.Date;

public class Candidature {
    private int idCandidature;
    private Utilisateur candidat;
    private OffreEmploi offre;
    private String statut; // PENDING, ACCEPTED, DECLINED
    private Date datePostulation;
    private String lettreMotivation;

    public Candidature() {
    }

    public Candidature(int idCandidature, Utilisateur candidat, OffreEmploi offre, String statut,
            Date datePostulation, String lettreMotivation) {
        this.idCandidature = idCandidature;
        this.candidat = candidat;
        this.offre = offre;
        this.statut = statut;
        this.datePostulation = datePostulation;
        this.lettreMotivation = lettreMotivation;
    }

    public Candidature(Utilisateur candidat, OffreEmploi offre, String statut, Date datePostulation,
            String lettreMotivation) {
        this.candidat = candidat;
        this.offre = offre;
        this.statut = statut;
        this.datePostulation = datePostulation;
        this.lettreMotivation = lettreMotivation;
    }

    public int getIdCandidature() {
        return idCandidature;
    }

    public void setIdCandidature(int idCandidature) {
        this.idCandidature = idCandidature;
    }

    public Utilisateur getCandidat() {
        return candidat;
    }

    public void setCandidat(Utilisateur candidat) {
        this.candidat = candidat;
    }

    public OffreEmploi getOffre() {
        return offre;
    }

    public void setOffre(OffreEmploi offre) {
        this.offre = offre;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Date getDatePostulation() {
        return datePostulation;
    }

    public void setDatePostulation(Date datePostulation) {
        this.datePostulation = datePostulation;
    }

    public String getLettreMotivation() {
        return lettreMotivation;
    }

    public void setLettreMotivation(String lettreMotivation) {
        this.lettreMotivation = lettreMotivation;
    }

    @Override
    public String toString() {
        return "Candidature{" +
                "idCandidature=" + idCandidature +
                ", candidat=" + (candidat != null ? candidat.getPrenom() + " " + candidat.getNom() : "null") +
                ", offre=" + (offre != null ? offre.getTitre() : "null") +
                ", statut='" + statut + '\'' +
                ", datePostulation=" + datePostulation +
                '}';
    }
}
