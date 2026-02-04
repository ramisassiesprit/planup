package Utils;

import Service.ServiceUtilisateur;
import Entite.Utilisateur;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class TestDat {
    public static void main(String[] args) {
        DataSource data1=DataSource.getInstance();
        DataSource data2=DataSource.getInstance();

        System.out.println(data1);
        System.out.println(data2);
        System.out.println(data1.getCon());
        System.out.println(data2.getCon());

        Connection c1=DataSource.getInstance().getCon();

        String email;
        String motDePasse;
        if (args.length >= 2) {
            email = args[0];
            motDePasse = args[1];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Email: ");
            email = scanner.nextLine();
            System.out.print("Mot de passe: ");
            motDePasse = scanner.nextLine();
        }

        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        try {
            Utilisateur utilisateur = serviceUtilisateur.authenticate(email, motDePasse);
            if (utilisateur != null) {
                System.out.println("Authentification réussie : " + utilisateur);
            } else {
                System.out.println("Authentification échouée : email ou mot de passe invalide");
            }
        } catch (SQLException e) {
            System.err.println("Erreur d'authentification : " + e.getMessage());
        }
    }
}
