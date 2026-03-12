package Entite;

import jakarta.persistence.*;

@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_project")
    private int idProject;
    private String name;
    private String type;
    @Transient
    private double progress;

    public Project() {
    }

    public Project(int idProject, String name, String type) {
        this.idProject = idProject;
        this.name = name;
        this.type = type;
    }

    public Project(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public int getIdProject() {
        return idProject;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "Project{" +
                "idProject=" + idProject +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
