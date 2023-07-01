package io.quarkus.workshop.superheroes.villain;

import jakarta.inject.Inject;
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
import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/api/villains")
public class VillainResource {

    Logger logger;

    VillainService service;

    public VillainResource(Logger logger, VillainService service) {
        this.logger = logger;
        this.service = service;
    }

    @GET
    @Path("/random")
    public RestResponse<Villain> getRandomVillain() {
        Villain villain = service.findRandomVillain();
        logger.debugf("Found random villain %s", villain);
        return RestResponse.ok(villain);
    }

    @GET
    public RestResponse<List<Villain>> getAllVillains() {
        List<Villain> villains = service.findAllVillains();
        logger.debugf("Total number of villains %d", villains.size());
        return RestResponse.ok(villains);
    }

    @GET
    @Path("/{id}")
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

    @POST
    public RestResponse<Void> createVillain(@Valid Villain villain, @Context UriInfo uriInfo) {
        villain = service.persistVillain(villain);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(villain.id));
        logger.debugf("New villain created with URI %s", builder.build().toString());
        return RestResponse.created(builder.build());
    }

    @PUT
    public RestResponse<Villain> updateVillain(@Valid Villain villain) {
        villain = service.updateVillain(villain);
        logger.debugf("Villain updated with new valued %s", villain);
        return RestResponse.ok(villain);
    }

    @DELETE
    @Path("/{id}")
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
