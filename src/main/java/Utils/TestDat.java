package Utils;

import java.sql.Connection;

public class TestDat {
    public static void main(String[] args) {
        DataSource data1=DataSource.getInstance();
        DataSource data2=DataSource.getInstance();

        System.out.println(data1);
        System.out.println(data2);
        System.out.println(data1.getCon());
        System.out.println(data2.getCon());

        Connection c1=DataSource.getInstance().getCon();
    }
}
