/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid.asu.rest.server.sjain.ws;

import java.net.URI;
import java.util.Random;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sid.asu.rest.server.sjain.jaxb.model.GradeBookItem;
import sid.asu.rest.server.sjain.jaxb.util.Converter;

/**
 * REST web service
 *
 * @author Siddharth
 */
@Path("GradeBook")
public class GradeBookResource {

    private static final Logger LOG = LoggerFactory.getLogger(GradeBookResource.class);

    private static GradeBookItem gradeBookItem;

    @Context
    private UriInfo context;

    public GradeBookResource() {
        LOG.info("Creating a Gradebook Resource");
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createGradeBookItem(String content) {
        LOG.info("Creating the instance Gradebook Resource {}", gradeBookItem);
        LOG.debug("POST request");
        LOG.debug("Request Content = {}", content);

        Response response;

        if (gradeBookItem == null) {
            LOG.debug("Attempting to create an Gradebook Resource and setting it to {}", content);

            try {
                gradeBookItem = (GradeBookItem) Converter.convertFromXmlToObject(content, GradeBookItem.class);
                LOG.debug("The XML {} was converted to the object {}", content, gradeBookItem);
                LOG.info("Creating a {} {} Status Response", Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase());

                // Id for newly created resource
                Random randomGenerator = new Random(12345);
                int gradeItemId = Math.abs(randomGenerator.nextInt(1000));
                gradeBookItem.setItemId(gradeItemId);

                String xmlString = Converter.convertFromObjectToXml(gradeBookItem, GradeBookItem.class);
                URI locationURI = URI.create(context.getAbsolutePath() + "/" + Integer.toString(gradeItemId));
                response = Response.status(Response.Status.CREATED).location(locationURI).entity(xmlString).build();

            } catch (JAXBException e) {
                LOG.info("Creating a {} {} Status Response", Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase());
                LOG.debug("XML is {} is incompatible with Gradebook Resource", content);

                response = Response.status(Response.Status.BAD_REQUEST).entity(content).build();
            } catch (RuntimeException e) {
                LOG.debug("Catch All exception");
                LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(content).build();
            }
        } else {
            LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
            LOG.debug("Cannot create Gradebook Resource as values is already set to {}", gradeBookItem);

            response = Response.status(Response.Status.CONFLICT).entity(content).build();
        }

        LOG.debug("Generated response {}", response);

        return response;
    }
}
