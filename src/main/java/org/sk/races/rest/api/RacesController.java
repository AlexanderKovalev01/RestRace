package org.sk.races.rest.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.sk.races.rest.entities.Gender;
import org.sk.races.rest.entities.Race;
import org.sk.races.rest.entities.RaceItem;
import org.sk.races.rest.entities.Runner;
import org.sk.races.rest.reader.TxtReader;

import java.util.HashMap;
import java.util.Map;

@Path("/races")
public class RacesController {
    private static Map<String, Race> races = new HashMap<>();

    @GET
    @Path("/init")
    @Produces(MediaType.APPLICATION_JSON)
    public Response initRaces() {
        Race randomMarathon = TxtReader.readRace("RandomMarathon.txt", "Marathon1");
        races.put("Marathon1", randomMarathon);
        return Response.ok(races).build();
    }

    @GET
    @Path("/{raceName}/runner/{runnerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRunner(@PathParam("raceName") String raceName,
                              @PathParam("runnerId") int runnerId) {
        Race race = races.get(raceName);
        if (race == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Race not found: " + raceName).build();
        }
        RaceItem runner = race.findRunnerById(runnerId);
        if (runner == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Runner with ID " + runnerId + " not found in race " + raceName).build();
        }
        return Response.ok(runner).build();
    }

    @POST
    @Path("/{raceName}/runner/{runnerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRunner(@PathParam("raceName") String raceName,
                              @PathParam("runnerId") int runnerId,
                              String runnerAsJson) {
        Race race = races.get(raceName);
        if (race == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Race not found: " + raceName).build();
        }
        JSONObject json;
        try {
            json = new JSONObject(runnerAsJson);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Runner JSON could not be parsed: " + e.getMessage()).build();
        }
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
                return Response.status(Response.Status.NOT_FOUND).entity("Runner with ID " + runnerId + " not found").build();
            }
            existing.setRunner(runner);
            existing.setTime(time);
            return Response.ok(existing).build();
        } else {
            RaceItem existing = race.findRunnerById(jsonId);
            if (existing != null) {
                return Response.status(Response.Status.CONFLICT).entity("Runner with ID " + jsonId + " already exists").build();
            }
            RaceItem newRunner = new RaceItem(jsonId, runner, time);
            race.addResult(newRunner);
            return Response.status(Response.Status.CREATED).entity(newRunner).build();
        }
    }
}




