package org.sk.races.rest.api;
import java.sql.*;

public class SqlRunner {

    public static void main(String[] args) throws ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection con = DriverManager .getConnection("jdbc:mysql://localhost:3306/w3schools", "root", "*****")) {
            String sql = "SELECT " +
                    "runners.first_name, " +
                    "runners.last_name, " +
                    "results.bib_number, " +
                    "marathons.name, " +
                    "results.finish_time " +
                    "FROM results " +
                    "INNER JOIN runners ON results.id = runners.id " +
                    "INNER JOIN marathons ON results.marathon_id = marathons.id";


            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                System.out.println("results:");
                while (rs.next()) {
                    String firstName = rs.getString(1);
                    String lastName = rs.getString(2);
                    int bibNumber = rs.getInt(3);
                    String marathonName = rs.getString(4);
                    Time finishTime = rs.getTime(5);

                    System.out.println(String.format("Runner %s %s with a BIB %s finished the race %s with time %s",firstName, lastName, bibNumber, marathonName, finishTime));
                }


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
