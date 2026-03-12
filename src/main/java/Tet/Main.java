package Tet;

import Entite.Project;
import Entite.Sprint;
import Service.ServiceProject;
import Service.ServiceSprint;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ServiceProject sp = new ServiceProject();
        ServiceSprint ss = new ServiceSprint();

        try {
            // Test Project
            Project p = new Project("Nouveau Projet de Test", "Desktop");
            sp.ajouter(p);

            List<Project> projects = sp.readAll();
            System.out.println("Projets existants :");
            projects.forEach(System.out::println);

            if (!projects.isEmpty()) {
                Project last = projects.get(projects.size() - 1);
                // Test Sprint
                Sprint s = new Sprint("Sprint de Test", last);
                ss.ajouter(s);

                System.out.println("\nSprints existants :");
                ss.readAll().forEach(System.out::println);
            }

        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
