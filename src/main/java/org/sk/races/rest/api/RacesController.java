package org.sk.races.rest.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.sk.races.rest.entities.Gender;
import org.sk.races.rest.entities.Race;
import org.sk.races.rest.entities.RaceItem;
import org.sk.races.rest.entities.Runner;
import org.sk.races.rest.reader.RaceCache;
import org.sk.races.rest.reader.TxtReader;

import java.util.*;

@Path("/races")
public class RacesController {
    private RaceCache raceCache = RaceCache.getInstance();

    @GET
    @Path("/init")
    @Produces(MediaType.APPLICATION_JSON)
    public Response initRaces() {
        Race randomMarathon = TxtReader.readRace("RandomMarathon.txt", "Marathon1");
        raceCache.addRace("Marathon1", randomMarathon);
        return Response.ok(RaceCache.getInstance().getRace("Marathon1")).build();
    }

    @GET
    @Path("/{raceName}/runner/{runnerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRunner(@PathParam("raceName") String raceName, @PathParam("runnerId") int runnerId) {
        Response result = null;
        Race race = raceCache.getRace(raceName);
        if (race == null) {
            result = Response.status(Response.Status.NOT_FOUND).entity("Race not found: " + raceName).build();
        } else {
            RaceItem runner = race.findRunnerById(runnerId);
            if (runner == null) {
                result = Response.status(Response.Status.NOT_FOUND).entity("Runner with ID " + runnerId + " not found in race " + raceName).build();
            } else {
                result = Response.ok(runner).build();
            }
        }
        return result;
    }

    @POST
    @Path("/{raceName}/runner/{runnerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRunner(@PathParam("raceName") String raceName, @PathParam("runnerId") int runnerId, String runnerAsJson) {
        Response result = null;
        Race race = raceCache.getRace(raceName);
        if (race == null) {
            result = Response.status(Response.Status.NOT_FOUND).entity("Race not found: " + raceName).build();
        } else {
            try {
                JSONObject json = new JSONObject(runnerAsJson);
                int jsonId = json.getInt("id");
                String fullName = json.getString("name") + " " + json.getString("lastName");
                int age = json.getInt("age");
                String country = json.getString("country");
                Gender gender = Gender.fromString(json.getString("gender"));
                int time = RaceItem.parseTimeToSeconds(json.getString("time"));

                Runner runner = new Runner(fullName, age, country, gender);

                if (runnerId == jsonId) {
                    RaceItem existing = race.findRunnerById(runnerId);
                    if (existing == null) {
                        result = Response.status(Response.Status.NOT_FOUND).entity("Runner with ID " + runnerId + " not found").build();
                    } else {
                        existing.setRunner(runner);
                        existing.setTime(time);
                        result = Response.ok(existing).build();
                    }
                } else {
                    RaceItem existing = race.findRunnerById(jsonId);
                    if (existing != null) {
                        result = Response.status(Response.Status.CONFLICT).entity("Runner with ID " + jsonId + " already exists").build();
                    } else {
                        RaceItem newRunner = new RaceItem(jsonId, runner, time);
                        race.addResult(newRunner);
                        result = Response.status(Response.Status.CREATED).entity(newRunner).build();
                    }
                }
            } catch (Exception e) {
                result = Response.status(Response.Status.BAD_REQUEST).entity("Invalid JSON: " + e.getMessage()).build();
            }
        }
        return result;
    }

    private List<Runner> consumerRunners = new ArrayList<>();
    private int nextId = 1;

    @GET
    @Path("/runners/producer/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response produceRunners(@PathParam("count") int count) {
        List<Runner> runners = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            runners.add(RandomRunner.generateRunner());
        }
        return Response.ok(runners).build();
    }

    @GET
    @Path("/runners/consumer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consumeRunners() {
        int count = new Random().nextInt(20) + 1;

        List<Runner> runners = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            runners.add(RandomRunner.generateRunner());
        }

        consumerRunners.addAll(runners);
        return Response.ok(consumerRunners).build();
    }
}



