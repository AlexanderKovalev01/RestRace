package org.sk.races.rest.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.sk.races.rest.entities.Gender;
import org.sk.races.rest.entities.Runner;
import org.sk.races.rest.reader.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            if (conn == null) { logger.severe("Failed to connect to the database.");
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
}