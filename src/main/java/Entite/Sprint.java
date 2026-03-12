package Entite;

import jakarta.persistence.*;

@Entity
@Table(name = "sprint")
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sprint")
    private int idSprint;
    private String name;
    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;
    @Transient
    private double progress;

    public Sprint() {
    }

    public Sprint(int idSprint, String name, Project project) {
        this.idSprint = idSprint;
        this.name = name;
        this.project = project;
    }

    public Sprint(String name, Project project) {
        this.name = name;
        this.project = project;
    }

    public int getIdSprint() {
        return idSprint;
    }

    public void setIdSprint(int idSprint) {
        this.idSprint = idSprint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "idSprint=" + idSprint +
                ", name='" + name + '\'' +
                ", project=" + (project != null ? project.getName() : "null") +
                '}';
    }
}
