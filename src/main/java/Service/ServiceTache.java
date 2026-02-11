package Service;

import Entite.Tache;
import Entite.Sprint;
import Entite.Utilisateur;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTache implements IService<Tache> {

    private Connection con = DataSource.getInstance().getCon();

    @Override
    public boolean ajouter(Tache t) throws SQLException {
        String query = "INSERT INTO tache (name, description, date_limite, duree, priorite, estimation, date_affectation, statut, id_sprint, cin_affecte) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, t.getName());
        ps.setString(2, t.getDescription());
        ps.setDate(3, t.getDateLimite());
        ps.setInt(4, t.getDuree());
        ps.setInt(5, t.getPriorite());
        ps.setInt(6, t.getEstimation());
        ps.setDate(7, t.getDateAffectation());
        ps.setString(8, (t.getStatut() == null) ? "PAS_ENCORE_FAITE" : t.getStatut());

        if (t.getSprint() != null)
            ps.setInt(9, t.getSprint().getIdSprint());
        else
            ps.setNull(9, Types.INTEGER);

        if (t.getAffecte() != null)
            ps.setInt(10, t.getAffecte().getCin());
        else
            ps.setNull(10, Types.INTEGER);

        return ps.executeUpdate() > 0;
    }

    @Override
    public boolean supprimer(Tache t) throws SQLException {
        String query = "DELETE FROM tache WHERE id_tache = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, t.getIdTache());
        return ps.executeUpdate() > 0;
    }

    @Override
    public boolean modifier(Tache t) throws SQLException {
        String query = "UPDATE tache SET name=?, description=?, date_limite=?, duree=?, priorite=?, estimation=?, date_affectation=?, statut=?, id_sprint=?, cin_affecte=? WHERE id_tache=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, t.getName());
        ps.setString(2, t.getDescription());
        ps.setDate(3, t.getDateLimite());
        ps.setInt(4, t.getDuree());
        ps.setInt(5, t.getPriorite());
        ps.setInt(6, t.getEstimation());
        ps.setDate(7, t.getDateAffectation());
        ps.setString(8, t.getStatut());

        if (t.getSprint() != null)
            ps.setInt(9, t.getSprint().getIdSprint());
        else
            ps.setNull(9, Types.INTEGER);

        if (t.getAffecte() != null)
            ps.setInt(10, t.getAffecte().getCin());
        else
            ps.setNull(10, Types.INTEGER);

        ps.setInt(11, t.getIdTache());
        return ps.executeUpdate() > 0;
    }

    @Override
    public Tache findbyId(int id) throws SQLException {
        String query = "SELECT * FROM tache WHERE id_tache = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSetToTache(rs);
        }
        return null;
    }

    @Override
    public List<Tache> readAll() throws SQLException {
        List<Tache> list = new ArrayList<>();
        String query = "SELECT * FROM tache";
        Statement ste = con.createStatement();
        ResultSet rs = ste.executeQuery(query);
        while (rs.next()) {
            list.add(mapResultSetToTache(rs));
        }
        return list;
    }

    public List<Tache> findByAssignee(int cin) throws SQLException {
        List<Tache> list = new ArrayList<>();
        String query = "SELECT * FROM tache WHERE cin_affecte = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, cin);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(mapResultSetToTache(rs));
        }
        return list;
    }

    public boolean updateStatus(int idTache, String newStatus) throws SQLException {
        String query = "UPDATE tache SET statut = ? WHERE id_tache = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, newStatus);
        ps.setInt(2, idTache);
        return ps.executeUpdate() > 0;
    }

    public boolean assignToUser(int idTache, int cin) throws SQLException {
        String query = "UPDATE tache SET cin_affecte = ?, date_affectation = ? WHERE id_tache = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, cin);
        ps.setDate(2, new Date(System.currentTimeMillis()));
        ps.setInt(3, idTache);
        return ps.executeUpdate() > 0;
    }

    private Tache mapResultSetToTache(ResultSet rs) throws SQLException {
        Tache t = new Tache();
        t.setIdTache(rs.getInt("id_tache"));
        t.setName(rs.getString("name"));
        t.setDescription(rs.getString("description"));
        t.setDateLimite(rs.getDate("date_limite"));
        t.setDuree(rs.getInt("duree"));
        t.setPriorite(rs.getInt("priorite"));
        t.setEstimation(rs.getInt("estimation"));
        t.setDateAffectation(rs.getDate("date_affectation"));
        t.setStatut(rs.getString("statut"));

        // Fetch Sprint and User if needed, or just set IDs for now
        // For a full implementation, you might want to join or call other services
        int idSprint = rs.getInt("id_sprint");
        if (!rs.wasNull()) {
            Sprint s = new Sprint();
            s.setIdSprint(idSprint);
            t.setSprint(s);
        }

        int cinAffecte = rs.getInt("cin_affecte");
        if (!rs.wasNull()) {
            Utilisateur u = new Utilisateur();
            u.setCin(cinAffecte);
            t.setAffecte(u);
        }

        return t;
    }
}
