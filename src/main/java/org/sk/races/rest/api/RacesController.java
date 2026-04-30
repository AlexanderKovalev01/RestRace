package org.sk.races.rest.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.sk.races.rest.entities.Race;
import org.sk.races.rest.entities.RaceItem;
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
        Race randomMarathon = TxtReader.readRace("RandomMarathon.txt", "Minsk Marathon");
        races.put("Minsk Marathon", randomMarathon);
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
}
