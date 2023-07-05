package io.quarkus.workshop.superheroes.villain;

import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/api/villains")
@Tag(name = "Villains")
public class VillainResource {

    Logger logger;

    VillainService service;

    public VillainResource(Logger logger, VillainService service) {
        this.logger = logger;
        this.service = service;
    }

    @Operation(summary = "Returns a random villain.")
    @GET
    @Path("/random")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Villain.class, required = true)))
    public RestResponse<Villain> getRandomVillain() {
        Villain villain = service.findRandomVillain();
        logger.debugf("Found random villain %s", villain);
        return RestResponse.ok(villain);
    }

    @Operation(summary = "Returns all the villains from the database")
    @GET
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Villain.class, type = SchemaType.ARRAY)))
    public RestResponse<List<Villain>> getAllVillains() {
        List<Villain> villains = service.findAllVillains();
        logger.debugf("Total number of villains %d", villains.size());
        return RestResponse.ok(villains);
    }

    @Operation(summary = "Returns a villain for a given identifier")
    @GET
    @Path("/{id}")
    @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Villain.class)))
    @APIResponse(responseCode = "204", description = "The villain is not found for a given identifier")
    public RestResponse<Villain> getVillain(@RestPath Long id) {
        Optional<Villain> villain = service.findVillainById(id);
        if (villain.isPresent()) {
            logger.debugf("Found villain %s", villain);
            return RestResponse.ok(villain.get());
        } else {
            logger.debugf("No villain found with id %d", id);
            return RestResponse.noContent();
        }
    }

    @Operation(summary = "Creates a valid villain")
    @POST
    @APIResponse(responseCode = "201", description = "The URI of the created villain", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    public RestResponse<Void> createVillain(@Valid Villain villain, @Context UriInfo uriInfo) {
        villain = service.persistVillain(villain);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(villain.id));
        logger.debugf("New villain created with URI %s", builder.build().toString());
        return RestResponse.created(builder.build());
    }

    @Operation(summary = "Updates an exiting  villain")
    @PUT
    @APIResponse(responseCode = "200", description = "The updated villain", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Villain.class)))
    public RestResponse<Villain> updateVillain(@Valid Villain villain) {
        villain = service.updateVillain(villain);
        logger.debugf("Villain updated with new valued %s", villain);
        return RestResponse.ok(villain);
    }

    @Operation(summary = "Deletes an exiting villain")
    @DELETE
    @Path("/{id}")
    @APIResponse(responseCode = "204")
    public RestResponse<Void> deleteVillain(@RestPath Long id) {
        service.deleteVillain(id);
        logger.debugf("Villain deleted with %d", id);
        return RestResponse.noContent();
    }

    @GET()
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello Villain Resource!";
    }
}
