package org.sk.races.rest.reader;

import org.sk.races.rest.entities.Race;
import org.sk.races.rest.entities.RaceItem;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvToDatabase {
    private static final Logger logger = Logger.getLogger(CsvToDatabase.class.getName());
    public static void main(String[] args) {
        CsvToDatabase service = new CsvToDatabase();
        boolean success = service.loadDataFromCSV();

        if (success) {
            logger.info("The data has been successfully loaded into the database!");
        } else {
            logger.severe("Error loading data!");
        }
    }

    public boolean loadDataFromCSV() {
        try {
            Race maraphon1 = CsvRaceReader.readRaceFromCSV("src/main/resources/maraphon 1.csv", "марафон 1");
            Race maraphon2 = CsvRaceReader.readRaceFromCSV("src/main/resources/maraphon 2.csv", "марафон 2");

            Connection conn = DatabaseConnection.getConnection();
                insertRunnersFromRace(conn,maraphon1);
                insertRunnersFromRace(conn,maraphon2);
                insertMarathons(conn);
                insertRacesFromRace(conn,maraphon1, 1);
                insertRacesFromRace(conn, maraphon2, 2);
            logger.info("All data has been uploaded");
                return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while loading data " + e.getMessage(), e);
            return false;
        }
    }
    private void insertRunnersFromRace(Connection conn, Race race) throws SQLException {
        String sql = "INSERT INTO runners (id, first_name, last_name, gender, age, country, city) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (RaceItem item : race.getResults()) {
                String fullName = item.getRunner().getName();
                String[] nameParts = fullName.split(" ");
                String firstName = nameParts.length > 0 ? nameParts[0] : "";
                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                pstmt.setInt(1, item.getId());
                pstmt.setString(2, firstName);
                pstmt.setString(3, lastName);
                pstmt.setString(4, item.getRunner().getGender().name());
                pstmt.setInt(5, item.getRunner().getAge());
                pstmt.setString(6, item.getRunner().getCountry());
                pstmt.setString(7, item.getRunner().getCity());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    private void insertMarathons(Connection conn) throws SQLException {
        String sql = "INSERT INTO marathons (id, name, country, city) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1);
            pstmt.setString(2, "марафон 1");
            pstmt.setString(3, "Страна 1");
            pstmt.setString(4, "Город 1");
            pstmt.addBatch();
            pstmt.setInt(1, 2);
            pstmt.setString(2, "марафон 2");
            pstmt.setString(3, "Страна 2");
            pstmt.setString(4, "Город 2");
            pstmt.addBatch();
            pstmt.executeBatch();
        }
    }
    private void insertRacesFromRace(Connection conn, Race race, int marathonId) throws SQLException {
        String sql = "INSERT INTO races (runner_id, marathon_id, finish_time, bib_number) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (RaceItem item : race.getResults()) {

                pstmt.setInt(1, item.getId());
                pstmt.setInt(2, marathonId);
                pstmt.setInt(3, item.getTime());
                pstmt.setInt(4, item.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
}