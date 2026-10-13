package org.sk.races.rest.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.sk.races.rest.entities.Gender;
import org.sk.races.rest.entities.Runner;
import org.sk.races.rest.reader.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/runners-db")
public class RunnersFromDbController {
    private static final Logger logger = Logger.getLogger(RunnersFromDbController.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRunnersFromDb() {
        List<Runner> runners = new ArrayList<>();

        String sql = "SELECT id, first_name, last_name, gender, age, country, city FROM runners";

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                logger.severe("Failed to connect to the database.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"status\": \"Database connection failed\"}").build();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String genderStr = rs.getString("gender");
                    int age = rs.getInt("age");
                    String country = rs.getString("country");
                    String city = rs.getString("city");
                    Gender gender = Gender.fromString(genderStr);
                    String fullName = firstName + " " + lastName;
                    Runner runner = new Runner(fullName, age, country, gender, city);

                    runners.add(runner);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving data from the database", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"status\": \"Error: " + e.getMessage() + "\"}").build();
        }
        return Response.ok(runners).build();
    }

    @GET
    @Path("/{marathonId}/runner/{runnerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRunnerById(
            @PathParam("marathonId") int marathonId,
            @PathParam("runnerId") int runnerId) {
        String sql = "SELECT r.first_name, r.last_name, r.gender, r.age, r.country, r.city " +
                "FROM races rc " +
                "JOIN runners r ON rc.runner_id = r.id " +
                "WHERE rc.marathon_id = ? AND rc.runner_id = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                logger.severe("Failed to connect to the database.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"status\": \"Database connection failed\"}").build();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, marathonId);
                pstmt.setInt(2, runnerId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                        Gender gender = Gender.fromString(rs.getString("gender"));

                        Runner runner = new Runner(
                                fullName,
                                rs.getInt("age"),
                                rs.getString("country"),
                                gender,
                                rs.getString("city")
                        );

                        logger.info(String.format("Found runner %d in marathon %d.", runnerId, marathonId));

                        return Response.ok(runner).build();

                    } else {
                        logger.warning(String.format("Runner %d not found in marathon %d", runnerId, marathonId));

                        return Response.status(Response.Status.NOT_FOUND).entity("{\"status\": \"Runner not found\"}").build();
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving runner from the database", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"status\": \"Error: " + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/age-range/{minAge}/{maxAge}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRunnersByAgeRange(
            @PathParam("minAge") int minAge,
            @PathParam("maxAge") int maxAge) {

        List<Runner> runners = new ArrayList<>();
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                logger.severe("Failed to connect to the database.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"status\": \"Database connection failed\"}").build();
            }

            conn.setAutoCommit(false);
            logger.info("Транзакция начата");


            String selectSql = "SELECT id, first_name, last_name, gender, age, country, city " + "FROM runners WHERE age BETWEEN ? AND ?";

            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setInt(1, minAge);
                pstmt.setInt(2, maxAge);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                        Gender gender = Gender.fromString(rs.getString("gender"));
                        Runner runner = new Runner(
                                fullName,
                                rs.getInt("age"),
                                rs.getString("country"),
                                gender,
                                rs.getString("city")
                        );
                        runners.add(runner);
                    }
                }
            }

            logger.info(String.format("Runners aged %d-%d found: %d", minAge, maxAge, runners.size()));


            String createTempSql = "CREATE TEMPORARY TABLE temp_runners_age (" +
                    "id INT, " +
                    "first_name VARCHAR(50), " +
                    "last_name VARCHAR(50), " +
                    "age INT, " +
                    "country VARCHAR(50), " +
                    "city VARCHAR(50)" +
                    ")";

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTempSql);
                logger.info("The temporary table has been created.");
            }


            String insertTempSql = "INSERT INTO temp_runners_age (id, first_name, last_name, age, country, city) " +
                    "SELECT id, first_name, last_name, age, country, city " +
                    "FROM runners WHERE age BETWEEN ? AND ?";

            try (PreparedStatement pstmt = conn.prepareStatement(insertTempSql)) {
                pstmt.setInt(1, minAge);
                pstmt.setInt(2, maxAge);
                int inserted = pstmt.executeUpdate();
                logger.info(String.format("The table contains: %d records", inserted));
            }

            Savepoint savepoint = conn.setSavepoint("before_null_check");
            logger.info("SAVEPOINT 'before_null_check' generated");


           /* String test = null;
            if (test.length() > 0) {
                logger.info("test break");
            }*/

            conn.commit();
            logger.info("The transaction has been confirmed");
            return Response.ok(runners).build();

        } catch (NullPointerException e) {

            logger.severe("NullPointerException! Rollback to savepoint");
            try {
                if (conn != null) {
                    conn.rollback();
                    logger.info("The transaction was rolled back.");
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error during rollback", ex);
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"status\": \"NullPointerException caught, transaction rolled back\"}").build();

        } catch (Exception e) {

            logger.log(Level.SEVERE, "Transaction error", e);
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error during rollback", ex);
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"status\": \"Error: " + e.getMessage() + "\"}").build();

        } finally {

            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error during autocommit recovery", e);
            }
        }
    }
}