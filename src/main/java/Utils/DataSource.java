package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private static DataSource ds;

    private String url = "jdbc:mysql://localhost:3306/esprit1alinfo1";
    private String user = "root";
    private String password = "";


    private DataSource() {
        try {
            Connection testCon = DriverManager.getConnection(url, user, password);
            System.out.println("[DataSource] Database connection test successful");
            testCon.close();
        } catch (SQLException e) {
            System.out.println("[DataSource] Database connection test failed: " + e.getMessage());
        }
    }

    public Connection getCon() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("[DataSource] Error creating connection: " + e.getMessage());
            return null;
        }
    }

    public static DataSource getInstance() {
        if (ds == null) {
            ds = new DataSource();
        }
        return ds;
    }
}
