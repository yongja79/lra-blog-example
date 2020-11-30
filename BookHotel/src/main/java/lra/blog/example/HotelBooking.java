package lra.blog.example;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.HeaderParam;

import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ParticipantStatus;
import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_CONTEXT_HEADER;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Path("/hotel")
public class HotelBooking {

    @GET
    @Path("/test")
    public String getTest() {
        return "Testing Hotel Booking";
    }

    @LRA(value = LRA.Type.MANDATORY, end=false)
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/book")
    public Response bookHotel(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId, String destination) {
        String message = "Starting Hotel booking to " + destination + " LRA with id: " + lraId + "\n";
        System.out.println(message);
        if (destination.equals("London")) {
            System.out.println("Hotel booked");
            return Response.ok().build();
        }
        else {
            System.out.println("Hotel booking failed");
            return Response.serverError().build();
        }
    }

    @Complete
    @Path("/complete")
    @PUT
    public Response completeHotel(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId, String userData) {
        String message = "Hotel Booking completed with LRA with id: " + lraId + "\n";
        System.out.println(message);
        return Response.ok(ParticipantStatus.Completed).build();
    }

    @Compensate
    @Path("/compensate")
    @PUT
    public Response compensateHotel(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId, String userData) {
        String message = "Hotel Booking compensated with LRA with id: " + lraId + "\n";
        System.out.println(message);
        return Response.ok(ParticipantStatus.Compensated.name()).build();
    }
}