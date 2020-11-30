package lra.blog.example;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ParticipantStatus;
import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_CONTEXT_HEADER;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Path("/flight")
public class FlightBooking {

    @GET
    @Path("/test")
    public String getTest() {
        return "Testing Flight Booking";
    }

    @LRA(value = LRA.Type.REQUIRED, end=false)
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/book")
    public Response bookFlight(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId, String destination) {
        String message = "Starting Flight booking to " + destination + " LRA with id: " + lraId + "\n";
        System.out.println(message);
        if (destination.equals("London") || destination.equals("Paris")) {
            System.out.println("Flight booked");
            return Response.ok().build();
        }
        else {
            System.out.println("Flight booking failed");
            return Response.serverError().build();
        }
    }

    @Complete
    @Path("/complete")
    @PUT
    public Response completeFlight(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId, String userData) {
        String message = "Flight Booking completed with LRA with id: " + lraId + "\n";
        System.out.println(message);
        return Response.ok(ParticipantStatus.Completed).build();
    }

    @Compensate
    @Path("/compensate")
    @PUT
    public Response compensateFlight(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId, String userData) {
        String message = "Flight Booking compensated with LRA with id: " + lraId + "\n";
        System.out.println(message);
        return Response.ok(ParticipantStatus.Compensated.name()).build();
    }
}
