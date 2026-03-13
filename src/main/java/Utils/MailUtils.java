package Utils;

import Entite.Utilisateur;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailUtils {

    // IMPORTANT: Replace with your actual credentials or move to a config file
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "feresbrahmi@gmail.com";
    private static final String SENDER_PASSWORD = "icju eybb gvwa eeay";

    public static void sendAccountDetails(Utilisateur user) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            message.setSubject("Bienvenue sur PlanUp - Vos informations de compte");

            String content = "<h2>Bienvenue " + user.getPrenom() + " " + user.getNom() + " !</h2>" +
                    "<p>Votre compte a été créé avec succès par l'administrateur. Voici vos informations de connexion :</p>" +
                    "<ul>" +
                    "<li><b>CIN :</b> " + user.getCin() + "</li>" +
                    "<li><b>Email :</b> " + user.getEmail() + "</li>" +
                    "<li><b>Mot de passe :</b> " + user.getMotDePasse() + "</li>" +
                    "<li><b>Téléphone :</b> " + user.getNumTel() + "</li>" +
                    "<li><b>Rôle :</b> " + user.getRole() + "</li>" +
                    "</ul>" +
                    "<p>Nous vous recommandons de changer votre mot de passe dès votre première connexion.</p>" +
                    "<p>Cordialement,<br>L'équipe PlanUp</p>";

            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("Email envoyé avec succès à " + user.getEmail());

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
}
