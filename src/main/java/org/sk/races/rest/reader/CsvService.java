package org.sk.races.rest.reader;

import org.sk.races.rest.entities.Race;
import org.sk.races.rest.entities.RaceItem;

import java.sql.*;

public class CsvService {

    private static final String URL = "jdbc:mysql://localhost:3306/w3schools";
    private static final String USER = "root";
    private static final String PASSWORD = "****";
    public static void main(String[] args) {
        CsvService service = new CsvService();
        boolean success = service.loadDataFromCSV();

        if (success) {
            System.out.println("The data has been successfully loaded into the database!");
        } else {
            System.out.println("Error loading data!");
        }
    }
    public boolean loadDataFromCSV() {
        try {
            Race minskRace = CsvRaceRead.readRaceFromCSV("src/main/resources/minsk_maraphon.csv", "Минский марафон");
            Race mogilevRace = CsvRaceRead.readRaceFromCSV("src/main/resources/mogliev_maraphon.csv", "Могилёвский марафон");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                conn.setAutoCommit(false);

                insertRunnersFromRace(conn, minskRace);
                insertRunnersFromRace(conn, mogilevRace);
                insertMarathons(conn);
                insertRacesFromRace(conn, minskRace, 1);
                insertRacesFromRace(conn, mogilevRace, 2);
                conn.commit();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
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
            pstmt.setString(2, "Минский марафон");
            pstmt.setString(3, "Беларусь");
            pstmt.setString(4, "Минск");
            pstmt.addBatch();
            pstmt.setInt(1, 2);
            pstmt.setString(2, "Могилёвский марафон");
            pstmt.setString(3, "Беларусь");
            pstmt.setString(4, "Могилёв");
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
                pstmt.setTime(3, item.getTime());
                pstmt.setInt(4, item.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
}