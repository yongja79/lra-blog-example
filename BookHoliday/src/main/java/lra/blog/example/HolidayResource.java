package lra.blog.example;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ParticipantStatus;
import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_CONTEXT_HEADER;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Path("/holiday")
public class HolidayResource {

    private Client flightClient;
    private Client hotelClient;
    private WebTarget flightTarget;
    private WebTarget hotelTarget;

    public HolidayResource() {
        flightClient = ClientBuilder.newClient();
        flightTarget = flightClient.target("http://localhost:9081/flight/lra/flight/book");
        hotelClient = ClientBuilder.newClient();
        hotelTarget = hotelClient.target("http://localhost:9081/hotel/lra/hotel/book");
    }

    @GET
    @Path("/test")
    public String getTest() {
        return "Testing the Holiday Booking";
    }

    @LRA(value = LRA.Type.REQUIRES_NEW)
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/book")
    public Response bookHoliday(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId, String destination ) {
        String message = "Starting Holiday booking to: " + destination + " LRA with id: " + lraId + "\n";
        System.out.println(message);

        Response flightResponse = flightTarget.request().post(Entity.entity(destination, MediaType.TEXT_PLAIN));
        String flightEntity = flightResponse.readEntity(String.class);

        Response hotelResponse = hotelTarget.request().post(Entity.entity(destination, MediaType.TEXT_PLAIN));
        String hotelEntity = hotelResponse.readEntity(String.class);

        return Response.ok().build();
    }

    @Complete
    @Path("/complete")
    @PUT
    public Response completeHoliday(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId) {
        String message = "Holiday Booking completed with LRA with id: " + lraId + "\n";
        System.out.println(message);
        return Response.ok(ParticipantStatus.Completed).build();
    }

    @Compensate
    @Path("/compensate")
    @PUT
    public Response compensateHoliday(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId) {
        String message = "Holiday Booking compensated with LRA with id: " + lraId + "\n";
        System.out.println(message);
        return Response.ok(ParticipantStatus.Compensated.name()).build();
    }
}