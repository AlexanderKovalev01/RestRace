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
import org.sk.races.rest.reader.RaceCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

@Path("/runners")
public class RandomRunner {
    private final Logger LOG = Logger.getLogger(RandomRunner.class.getName());
    private final RaceCache raceCache = RaceCache.getInstance();
    private final String[] NAMES = {"John", "Jane", "Mike", "Sarah", "Tom", "Anna", "David", "Maria"};
    private final String[] SURNAMES = {"Smith", "Johnson", "Brown", "Lee", "Kim", "Chen"};
    private final String[] COUNTRIES = {"USA", "UK", "Germany", "France", "Japan"};
    private final Random random = new Random();
    private String host;
    private String port;

    public RandomRunner() {
        host = getProperty("host");
        port = getProperty("port");
    }


    @GET
    @Path("/producer/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response produceRunners(@PathParam("count") int count) {
        List<Runner> runners = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            runners.add(generateRunner());
        }
        return Response.ok(runners).build();
    }

    private String getProperty(String propName) {
        InputStream is = RandomRunner.class.getClassLoader().getResourceAsStream("raceapp.properties");

        Properties appProps = new Properties();
        try {
            appProps.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String propValue = appProps.getProperty(propName);
        LOG.info(String.format("Loaded property: %s=%s", propName, propValue));

        return propValue;
    }

    @GET
    @Path("/consumer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consumeRunners() {
        try {
            int count = random.nextInt(20) + 1;

            HttpClient client = HttpClient.newHttpClient();
            String url = String.format("http://%s:%s/rest/api/runners/producer/", host, port);
            HttpRequest request = HttpRequest.newBuilder(URI.create(url + count)).GET().build();
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
            raceCache.addRunners(runners);
            return Response.ok(raceCache.getAllRunners()).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error: " + e.getMessage()).build();
        }
    }

    public Runner generateRunner() {
        String name = NAMES[random.nextInt(NAMES.length)] + " " + SURNAMES[random.nextInt(SURNAMES.length)];
        int age = 18 + random.nextInt(50);
        String country = COUNTRIES[random.nextInt(COUNTRIES.length)];
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        return new Runner(name, age, country, gender);
    }

}

