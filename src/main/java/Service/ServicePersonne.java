package Service;

import Entite.Personne;
import Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePersonne implements IService<Personne> {
    private Connection connect= DataSource.getInstance().getCon();
    private Statement st;


    public ServicePersonne(){
        try {
            st=connect.createStatement();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public boolean ajouter(Personne personne) throws SQLException {
        boolean test=false;
        int res=-1;
        String req="INSERT INTO `personne` (`nom`, `prenom`, `age`) VALUES ('"+personne.getNom()+"', '"+personne.getPrenom()+"', '"+personne.getAge()+"');";
    res=st.executeUpdate(req);
    if(res>0)
        test=true;


    return test;
    }

    @Override
    public boolean supprimer(Personne personne) throws SQLException {
        return false;
    }

    @Override
    public boolean modifier(Personne personne) throws SQLException {
        return false;
    }

    @Override
    public Personne findbyId(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Personne> readAll() throws SQLException {
        List<Personne> list=new ArrayList<>();

        String query="SELECT * FROM `personne`";

        ResultSet rest = st.executeQuery(query);
            while (rest.next()) {
                int id = rest.getInt(1);
                String nom = rest.getString("nom");
                String prenom = rest.getString(3);
                int age = rest.getInt("age");
                Personne personne = new Personne(id,nom,prenom,age);
                list.add(personne);
            }


        return list;
    }
}
