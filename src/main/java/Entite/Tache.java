package Entite;

import java.sql.Date;

public class Tache {
    private int idTache;
    private String name;
    private String description;
    private Date dateLimite;
    private int duree;
    private int priorite;
    private int estimation;
    private Date dateAffectation;
    private Sprint sprint;
    private Utilisateur affecte;

    public Tache() {
    }

    public Tache(int idTache, String name, String description, Date dateLimite, int duree, int priorite, int estimation,
            Date dateAffectation, Sprint sprint, Utilisateur affecte) {
        this.idTache = idTache;
        this.name = name;
        this.description = description;
        this.dateLimite = dateLimite;
        this.duree = duree;
        this.priorite = priorite;
        this.estimation = estimation;
        this.dateAffectation = dateAffectation;
        this.sprint = sprint;
        this.affecte = affecte;
    }

    public Tache(String name, String description, Date dateLimite, int duree, int priorite, int estimation,
            Date dateAffectation, Sprint sprint, Utilisateur affecte) {
        this.name = name;
        this.description = description;
        this.dateLimite = dateLimite;
        this.duree = duree;
        this.priorite = priorite;
        this.estimation = estimation;
        this.dateAffectation = dateAffectation;
        this.sprint = sprint;
        this.affecte = affecte;
    }

    public int getIdTache() {
        return idTache;
    }

    public void setIdTache(int idTache) {
        this.idTache = idTache;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(Date dateLimite) {
        this.dateLimite = dateLimite;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

    public int getEstimation() {
        return estimation;
    }

    public void setEstimation(int estimation) {
        this.estimation = estimation;
    }

    public Date getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(Date dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public Utilisateur getAffecte() {
        return affecte;
    }

    public void setAffecte(Utilisateur affecte) {
        this.affecte = affecte;
    }

    @Override
    public String toString() {
        return "Tache{" +
                "idTache=" + idTache +
                ", name='" + name + '\'' +
                ", dateLimite=" + dateLimite +
                ", sprint=" + (sprint != null ? sprint.getName() : "null") +
                ", affecte=" + (affecte != null ? affecte.getNom() : "null") +
                '}';
    }
}
