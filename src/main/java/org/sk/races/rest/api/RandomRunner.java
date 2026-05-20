package org.sk.races.rest.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sk.races.rest.entities.Gender;
import org.sk.races.rest.entities.Runner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Path("/runners")
public class RandomRunner {
    private static List<Runner> runnerStorages = new ArrayList<>();
    private static final String[] NAMES = {"John", "Jane", "Mike", "Sarah", "Tom", "Anna", "David", "Maria"};
    private static final String[] SURNAMES = {"Smith", "Johnson", "Brown", "Lee", "Kim", "Chen"};
    private static final String[] COUNTRIES = {"USA", "UK", "Germany", "France", "Japan"};
    private static final Random random = new Random();

    @GET
    @Path("/producer/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response produceRunners(@PathParam("count") int count) {
        List<Runner> runners = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            runners.add(RandomRunner.generateRunner());
        }
        return Response.ok(runners).build();
    }

    @GET
    @Path("/consumer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consumeRunners() {
        try {
            int count = random.nextInt(20) + 1;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/rest/api/runners/producer/" + count)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            JSONArray jsonArray = new JSONArray(json);
            List<Runner> runners = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String name = object.getString("name");
                int age = object.getInt("age");
                String genderStr = object.getString("gender");
                Gender gender = Gender.fromString(genderStr);
                String country = object.getString("country");
                Runner runner = new Runner(name, age, country, gender);
                runners.add(runner);
            }
            runnerStorages.addAll(runners);
            return Response.ok(runnerStorages).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error: " + e.getMessage()).build();
        }
    }

    public static Runner generateRunner() {
        String name = NAMES[random.nextInt(NAMES.length)] + " " + SURNAMES[random.nextInt(SURNAMES.length)];
        int age = 18 + random.nextInt(50);
        String country = COUNTRIES[random.nextInt(COUNTRIES.length)];
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        return new Runner(name, age, country, gender);
    }

}

