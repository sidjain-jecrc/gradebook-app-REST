/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid.asu.rest.server.sjain.ws;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sid.asu.rest.server.sjain.jaxb.model.GradeBookItem;
import sid.asu.rest.server.sjain.jaxb.model.Student;
import sid.asu.rest.server.sjain.jaxb.util.Converter;

/**
 * REST web service
 *
 * @author Siddharth
 */
@Path("GradeBook")
public class GradeBookResource {

    private static final Logger LOG = LoggerFactory.getLogger(GradeBookResource.class);

    private static HashMap<Integer, GradeBookItem> gradeBookItemMap = null;

    @Context
    private UriInfo context;

    public GradeBookResource() {
        LOG.info("Creating a Gradebook Resource");
        gradeBookItemMap = new HashMap<Integer, GradeBookItem>() {
            {
                put(500, new GradeBookItem());
                put(501, new GradeBookItem());
                put(502, new GradeBookItem());
            }
        };
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createGradeBookItem(@PathParam("id") String studentId, String content) {

        LOG.debug("POST request");
        LOG.debug("Request Content = {}", content);

        GradeBookItem requestedGradeBookItem = null;
        Response response = null;
        boolean doesExist = false;
        int requestedStudentId = Integer.parseInt(studentId);

        try {
            requestedGradeBookItem = (GradeBookItem) Converter.convertFromXmlToObject(content, GradeBookItem.class);
            int requestItemId = requestedGradeBookItem.getItemId();

            GradeBookItem existingItem = gradeBookItemMap.get(requestItemId);
            List<Student> existingStudents = existingItem.getStudents();

            // checking if grade item and oldStudent record already exists
            for (Student existStudent : existingStudents) {
                if (existStudent.getId() == requestedStudentId) {
                    doesExist = true;
                    LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                    LOG.debug("Cannot create Gradebook Resource as student already exists {}", content);
                    response = Response.status(Response.Status.CONFLICT).entity(content).build();
                    LOG.debug("Generated response {}", response);
                }
            }

            // if grade item does not exists, creating it and setting it in gradeItemMap
            if (!doesExist) {
                LOG.debug("Attempting to create a student grade item resource and setting it to {}", content);

                // retrieving details of oldStudent from request
                Student requestedStudent = requestedGradeBookItem.getStudents().get(0);

                // creating a new oldStudent and updating the grade item
                Student newStudent = new Student();
                newStudent.setId(requestedStudentId);
                newStudent.setName(requestedStudent.getName());
                newStudent.setScore(requestedStudent.getScore());
                newStudent.setFeedback(requestedStudent.getFeedback());
                newStudent.setGraded(true);
                newStudent.setAppealStatus(Student.GradeAppeal.NOT_APPEALED);
                existingStudents.add(newStudent);
                existingItem.setStudents(existingStudents);
                gradeBookItemMap.put(requestItemId, existingItem);

                LOG.info("Creating a {} {} Status Response", Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase());
                String xmlString = Converter.convertFromObjectToXml(existingItem, GradeBookItem.class);
                URI locationURI = URI.create(context.getAbsolutePath() + "/" + studentId);
                response = Response.status(Response.Status.CREATED).location(locationURI).entity(xmlString).build();
            }
        } catch (JAXBException e) {
            LOG.error("Error: " + e);
            LOG.info("Creating a {} {} Status Response", Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase());
            LOG.debug("XML is {} is incompatible with Gradebook Resource", content);

            response = Response.status(Response.Status.BAD_REQUEST).entity(content).build();
        } catch (RuntimeException e) {
            LOG.error("Error: " + e);
            LOG.debug("Catch All exception");
            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(content).build();
        }

        LOG.debug("Generated response {}", response);
        return response;
    }

    @GET
    @Path("{itemId}/{sid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getGradeBookItem(@PathParam("itemId") String itemId, @PathParam("sid") String studentId) {

        LOG.debug("GET request");
        LOG.debug("PathParam id = {}", studentId);

        Response response = null;
        boolean doesExist = false;
        int requestedStudentId = Integer.parseInt(studentId);
        int requestedItemId = Integer.parseInt(itemId);

        try {
            GradeBookItem existingItem = gradeBookItemMap.get(requestedItemId);
            if (existingItem == null) {
                LOG.info("Creating a {} {} Status Response", Response.Status.GONE.getStatusCode(), Response.Status.GONE.getReasonPhrase());
                LOG.debug("No GradeBook item Resource to return");

                response = Response.status(Response.Status.GONE).entity("No Gradebook item resource to return").build();

            } else { // if grading item exists, then check if the requested oldStudent details are present

                List<Student> existingStudents = existingItem.getStudents();
                for (Student student : existingStudents) {
                    if (student.getId() == requestedStudentId) {
                        doesExist = true;
                        LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                        LOG.debug("Retrieving the student resource {}", student.getId() + ", " + student.getName());

                        String xmlString = Converter.convertFromObjectToXml(student, Student.class);
                        response = Response.status(Response.Status.OK).entity(xmlString).build();
                    }
                }

                // if the requested oldStudent is not found
                if (!doesExist) {
                    LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());
                    response = Response.status(Response.Status.NOT_FOUND).entity("No Gradebook Resource to return").build();
                }
            }
        } catch (Exception e) {
            LOG.error("Error: " + e);
            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Requested resource not found").build();
        }

        LOG.debug("Returning the value {}", response);
        return response;
    }

    @PUT
    @Path("{sid}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response updateGradeBookItem(@PathParam("sid") String studentId, String content) {
        LOG.debug("PUT request");
        LOG.debug("PathParam id = {}", studentId);
        LOG.debug("Request Content = {}", content);

        Response response = null;
        boolean doesExist = false;
        int requestedStudentId = Integer.parseInt(studentId);
        GradeBookItem requestedGradeBookItem = null;

        try {
            requestedGradeBookItem = (GradeBookItem) Converter.convertFromXmlToObject(content, GradeBookItem.class);
            int requestedItemId = requestedGradeBookItem.getItemId();
            GradeBookItem existingItem = gradeBookItemMap.get(requestedItemId);

            // checking if the grading item exists or not
            if (existingItem == null) {
                LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                LOG.debug("Cannot update gradebook item resource {} as it has not yet been created");
                response = Response.status(Response.Status.CONFLICT).entity(content).build();

            } else { // checking if the student record that needs to be updated exists

                Student updatedStudent = requestedGradeBookItem.getStudents().get(0);
                List<Student> existingStudents = existingItem.getStudents();
                for (Student oldStudent : existingStudents) {
                    if (oldStudent.getId() == requestedStudentId) {
                        doesExist = true;

                        oldStudent.setName(updatedStudent.getName());
                        oldStudent.setFeedback(updatedStudent.getFeedback());
                        oldStudent.setScore(updatedStudent.getScore());

                        existingItem.setStudents(existingStudents);
                        gradeBookItemMap.put(requestedItemId, existingItem);

                        LOG.debug("The XML {} was converted to the object {}", content, oldStudent);

                        LOG.debug("Updated Gradebook student resource to {}", oldStudent);
                        LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());

                        String xmlString = Converter.convertFromObjectToXml(oldStudent, Student.class);
                        response = Response.status(Response.Status.OK).entity(xmlString).build();
                    }
                }

                if (!doesExist) {
                    LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());
                    LOG.debug("Cannot update gradebook student item resource as its not available");
                    response = Response.status(Response.Status.NOT_FOUND).entity(content).build();
                }
            }
        } catch (JAXBException e) {
            LOG.error("Error: " + e);
            LOG.info("Creating a {} {} Status Response", Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase());
            LOG.debug("XML is {} is incompatible with Gradebook Resource", content);

            response = Response.status(Response.Status.BAD_REQUEST).entity(content).build();

        } catch (RuntimeException e) {
            LOG.error("Error: " + e);
            LOG.debug("Catch All exception");
            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(content).build();
        }

        LOG.debug("Generated response {}", response);
        return response;
    }

    @DELETE
    @Path("{itemId}/{sid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response deleteResource(@PathParam("itemId") String itemId, @PathParam("sid") String studentId) {
        LOG.debug("DELETE request");
        LOG.debug("PathParam id = {}", itemId + ", " + studentId);

        Response response = null;
        boolean isFound = false;

        if (gradeBookItem == null) {
            LOG.info("Creating a {} {} Status Response", Response.Status.GONE.getStatusCode(), Response.Status.GONE.getReasonPhrase());
            LOG.debug("No Gradebook Resource to delete");

            response = Response.status(Response.Status.GONE).build();
        } else {
            List<Student> students = gradeBookItem.getStudents();
            for (Student student : students) {
                if (student.getId() == Integer.parseInt(id)) {
                    isFound = true;
                    LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                    LOG.debug("Deleting the student Resource {}", student);

                    students.remove(student);
                    gradeBookItem.setStudents(students);
                    response = Response.status(Response.Status.OK).build();
                }
            }
            if (!isFound) {
                LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());
                LOG.debug("Failed to retrieve the requested resource {}");

                response = Response.status(Response.Status.NOT_FOUND).build();
            }
        }

        LOG.debug("Generated response {}", response);
        return response;
    }
}
