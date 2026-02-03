import Entite.Project;
import Entite.Sprint;
import Service.ServiceProject;
import Service.ServiceSprint;

import java.sql.SQLException;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        ServiceProject sp = new ServiceProject();
        ServiceSprint ss = new ServiceSprint();

        try {
            // 1. Test Project
            System.out.println("--- Test Project ---");
            Project p1 = new Project("Nouveau Projet", "Web");
            if (sp.ajouter(p1)) {
                System.out.println("Projet ajouté avec succès !");
            }

            List<Project> projects = sp.readAll();
            System.out.println("Liste des projets :");
            for (Project p : projects) {
                System.out.println(p);
            }

            if (!projects.isEmpty()) {
                Project lastProject = projects.get(projects.size() - 1);

                // 2. Test Sprint
                System.out.println("\n--- Test Sprint ---");
                Sprint s1 = new Sprint("Sprint 1", lastProject);
                if (ss.ajouter(s1)) {
                    System.out.println("Sprint ajouté avec succès !");
                }

                List<Sprint> sprints = ss.readAll();
                System.out.println("Liste des sprints :");
                for (Sprint s : sprints) {
                    System.out.println(s);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
    }
}
