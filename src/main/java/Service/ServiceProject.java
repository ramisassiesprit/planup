package Service;

import Entite.Project;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProject implements IService<Project> {
    private Connection connect = DataSource.getInstance().getCon();

    @Override
    public boolean ajouter(Project project) throws SQLException {
        String req = "INSERT INTO `project` (`name`, `type`) VALUES (?, ?);";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setString(1, project.getName());
        pst.setString(2, project.getType());
        int res = pst.executeUpdate();
        return res > 0;
    }

    @Override
    public boolean supprimer(Project project) throws SQLException {
        String req = "DELETE FROM `project` WHERE `id_project` = ?;";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setInt(1, project.getIdProject());
        int res = pst.executeUpdate();
        return res > 0;
    }

    @Override
    public boolean modifier(Project project) throws SQLException {
        String req = "UPDATE `project` SET `name` = ?, `type` = ? WHERE `id_project` = ?;";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setString(1, project.getName());
        pst.setString(2, project.getType());
        pst.setInt(3, project.getIdProject());
        int res = pst.executeUpdate();
        return res > 0;
    }

    @Override
    public Project findbyId(int id) throws SQLException {
        String req = "SELECT * FROM `project` WHERE `id_project` = ?;";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return new Project(rs.getInt("id_project"), rs.getString("name"), rs.getString("type"));
        }
        return null;
    }

    @Override
    public List<Project> readAll() throws SQLException {
        List<Project> list = new ArrayList<>();
        String req = "SELECT * FROM `project`;";
        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            list.add(new Project(rs.getInt("id_project"), rs.getString("name"), rs.getString("type")));
        }
        return list;
    }
}
