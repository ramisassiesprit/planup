package Tet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class LoginApp extends Application {

    @Override
    public void start(Stage stage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger Login.fxml", e);
        }

        Scene scene = new Scene(root, 420, 180);
        stage.setTitle("Login - PlanUp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Initialize Hibernate and update database schema
        Utils.HibernateUtil.getSessionFactory();
        launch(args);
    }
}
