package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private static DataSource ds;
    private Connection con;

    private String url = "jdbc:mysql://localhost:3306/esprit1alinfo1";
    private String user = "root";
    private String password = "";

    private DataSource() {
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    public Connection getCon() {
        return con;
    }

    public static DataSource getInstance() {
        if (ds == null) {
            ds = new DataSource();
        }
        return ds;
    }
}
