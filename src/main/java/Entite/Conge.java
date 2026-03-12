package Entite;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "conge")
public class Conge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conge")
    private int idConge;
    @ManyToOne
    @JoinColumn(name = "cin")
    private Utilisateur utilisateur;
    private String type;
    @Column(name = "date_debut")
    private Date dateDebut;
    @Column(name = "date_fin")
    private Date dateFin;
    @Column(name = "nbr_jours")
    private int nbrJours;
    private String justificatif;
    private String statut;
    @Column(name = "solde_conge")
    private int soldeConge;
    @Column(name = "conge_solde")
    private int congeSolde;

    public Conge() {
    }

    public Conge(int idConge, Utilisateur utilisateur, String type, Date dateDebut, Date dateFin, int nbrJours,
            String justificatif, String statut, int soldeConge, int congeSolde) {
        this.idConge = idConge;
        this.utilisateur = utilisateur;
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nbrJours = nbrJours;
        this.justificatif = justificatif;
        this.statut = statut;
        this.soldeConge = soldeConge;
        this.congeSolde = congeSolde;
    }

    public Conge(Utilisateur utilisateur, String type, Date dateDebut, Date dateFin, int nbrJours, String justificatif,
            String statut, int soldeConge, int congeSolde) {
        this.utilisateur = utilisateur;
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nbrJours = nbrJours;
        this.justificatif = justificatif;
        this.statut = statut;
        this.soldeConge = soldeConge;
        this.congeSolde = congeSolde;
    }

    public int getIdConge() {
        return idConge;
    }

    public void setIdConge(int idConge) {
        this.idConge = idConge;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public int getNbrJours() {
        return nbrJours;
    }

    public void setNbrJours(int nbrJours) {
        this.nbrJours = nbrJours;
    }

    public String getJustificatif() {
        return justificatif;
    }

    public void setJustificatif(String justificatif) {
        this.justificatif = justificatif;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getSoldeConge() {
        return soldeConge;
    }

    public void setSoldeConge(int soldeConge) {
        this.soldeConge = soldeConge;
    }

    public int getCongeSolde() {
        return congeSolde;
    }

    public void setCongeSolde(int congeSolde) {
        this.congeSolde = congeSolde;
    }

    @Override
    public String toString() {
        return "Conge{" +
                "idConge=" + idConge +
                ", utilisateur=" + (utilisateur != null ? utilisateur.getNom() : "null") +
                ", type='" + type + '\'' +
                ", nbrJours=" + nbrJours +
                ", statut='" + statut + '\'' +
                '}';
    }
}
