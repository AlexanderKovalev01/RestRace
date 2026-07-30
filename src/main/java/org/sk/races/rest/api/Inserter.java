package org.sk.races.rest.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Inserter {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/w3schools";
        String user = "root";
        String password = "*****";
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement()) {
            String insertRunners = "INSERT INTO runners (id, first_name, last_name, gender, age, country, city) VALUES "
                    + "(374, 'Kacper', 'Ivanov', 'M', 34, 'China', 'Chengdu'), "
                    + "(892, 'Giulia', 'Gomes', 'F', 20, 'Sweden', 'Stockholm'), "
                    + "(156, 'Ingrid', 'Jones', 'F', 18, 'Spain', 'Seville'), "
                    + "(743, 'Alex', 'Petrov', 'M', 25, 'Russia', 'Moscow'), "
                    + "(218, 'Maria', 'Silva', 'F', 22, 'Brazil', 'Sao Paulo'), "
                    + "(589, 'John', 'Smith', 'M', 40, 'USA', 'New York')";

            int rowsAffectedRunners = stmt.executeUpdate(insertRunners);

            String insertMarathons = "INSERT INTO marathons (id, name, country, city) VALUES "
                    + "(1, 'Минский марафон', 'Беларусь', 'Минск'), "
                    + "(2, 'Могилёвский марафон', 'Беларусь', 'Могилёв')";

            int rowsAffectedMarathons = stmt.executeUpdate(insertMarathons);

            String insertRaces = "INSERT INTO races (runner_id, marathon_id) VALUES "
                    + "(374, 1), "
                    + "(892, 1), "
                    + "(156, 1), "
                    + "(743, 2), "
                    + "(218, 2), "
                    + "(589, 2)";
            int rowsAffectedRaces = stmt.executeUpdate(insertRaces);

            String insertResults = "INSERT INTO results (runner_id, marathon_id, finish_time, bib_number) VALUES "
                    + "(374, 1, '05:20:50', 101), "
                    + "(892, 1, '04:57:53', 102), "
                    + "(156, 1, '05:54:15', 103), "
                    + "(743, 2, '04:30:15', 202), "
                    + "(218, 2, '04:45:30', 203), "
                    + "(589, 2, '03:55:40', 204)";

            int rowsAffectedResults = stmt.executeUpdate(insertResults);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}