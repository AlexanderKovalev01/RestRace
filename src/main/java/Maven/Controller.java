package Maven;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/races")
public class Controller {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getRace(@PathParam("id") int id){
        return Response.ok().entity(String.format("You've got an ID %s here and you can search for a race or runner by this Id", id)).build();
    }

}