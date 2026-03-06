package Service;

import Entite.Project;
import Entite.Sprint;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceSprint implements IService<Sprint> {
    private Connection connect = DataSource.getInstance().getCon();

    @Override
    public boolean ajouter(Sprint sprint) throws SQLException {
        String req = "INSERT INTO `sprint` (`name`, `id_project`) VALUES (?, ?);";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setString(1, sprint.getName());
        pst.setInt(2, sprint.getProject().getIdProject());
        int res = pst.executeUpdate();
        return res > 0;
    }

    @Override
    public boolean supprimer(Sprint sprint) throws SQLException {
        String req = "DELETE FROM `sprint` WHERE `id_sprint` = ?;";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setInt(1, sprint.getIdSprint());
        int res = pst.executeUpdate();
        return res > 0;
    }

    @Override
    public boolean modifier(Sprint sprint) throws SQLException {
        String req = "UPDATE `sprint` SET `name` = ?, `id_project` = ? WHERE `id_sprint` = ?;";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setString(1, sprint.getName());
        pst.setInt(2, sprint.getProject().getIdProject());
        pst.setInt(3, sprint.getIdSprint());
        int res = pst.executeUpdate();
        return res > 0;
    }

    @Override
    public Sprint findbyId(int id) throws SQLException {
        String req = "SELECT s.*, p.name as project_name, p.type as project_type FROM `sprint` s " +
                "JOIN `project` p ON s.id_project = p.id_project WHERE s.id_sprint = ?;";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            Project p = new Project(rs.getInt("id_project"), rs.getString("project_name"),
                    rs.getString("project_type"));
            return new Sprint(rs.getInt("id_sprint"), rs.getString("name"), p);
        }
        return null;
    }

    @Override
    public List<Sprint> readAll() throws SQLException {
        List<Sprint> list = new ArrayList<>();
        String req = "SELECT s.*, p.name as project_name, p.type as project_type FROM `sprint` s " +
                "JOIN `project` p ON s.id_project = p.id_project;";
        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            Project p = new Project(rs.getInt("id_project"), rs.getString("project_name"),
                    rs.getString("project_type"));
            list.add(new Sprint(rs.getInt("id_sprint"), rs.getString("name"), p));
        }
        return list;
    }

    /**
     * Récupère tous les sprints d'un projet spécifique
     * 
     * @param idProject L'ID du projet
     * @return Liste des sprints du projet
     */
    public List<Sprint> getSprintsByProject(int idProject) throws SQLException {
        List<Sprint> list = new ArrayList<>();
        String req = "SELECT s.*, p.name as project_name, p.type as project_type FROM `sprint` s " +
                "JOIN `project` p ON s.id_project = p.id_project WHERE s.id_project = ?;";
        PreparedStatement pst = connect.prepareStatement(req);
        pst.setInt(1, idProject);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Project p = new Project(rs.getInt("id_project"), rs.getString("project_name"),
                    rs.getString("project_type"));
            list.add(new Sprint(rs.getInt("id_sprint"), rs.getString("name"), p));
        }
        return list;
    }

    public double getProjectProgress(int idProject) throws SQLException {
        String query = "SELECT id_sprint FROM sprint WHERE id_project = ?";
        List<Integer> sprintIds = new ArrayList<>();
        try (PreparedStatement ps = connect.prepareStatement(query)) {
            ps.setInt(1, idProject);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sprintIds.add(rs.getInt("id_sprint"));
                }
            }
        }

        if (sprintIds.isEmpty())
            return 0.0;

        ServiceTache serviceTache = new ServiceTache();
        double totalProgress = 0;
        for (int idSprint : sprintIds) {
            totalProgress += serviceTache.getSprintProgress(idSprint);
        }

        return totalProgress / sprintIds.size();
    }
}
