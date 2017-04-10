/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid.asu.rest.server.sjain.ws;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
@Path("gradebook")
public class GradeBookResource {

    private static final Logger LOG = LoggerFactory.getLogger(GradeBookResource.class);
    private static final int ASSIGNMENTS = 500;
    private static final int MID_TERM = 501;
    private static final int FINAL = 502;
    private static HashMap<Integer, GradeBookItem> gradeBookItemMap = new HashMap<Integer, GradeBookItem>() {
        {
            put(ASSIGNMENTS, new GradeBookItem());
            put(MID_TERM, new GradeBookItem());
            put(FINAL, new GradeBookItem());
        }
    };
    ;

    @Context
    private UriInfo context;

    public GradeBookResource() {
        LOG.info("Creating a Gradebook Resource");
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createGradeBookItem(String content) {

        LOG.debug("POST request");
        LOG.debug("Request Content = {}", content);

        GradeBookItem requestedGradeBookItem = null;
        Response response = null;
        boolean doesExist = false;

        try {
            requestedGradeBookItem = (GradeBookItem) Converter.convertFromXmlToObject(content, GradeBookItem.class);
            int requestItemId = requestedGradeBookItem.getItemId();
            int requestedStudentId = requestedGradeBookItem.getStudents().get(0).getId();
            LOG.debug("Request itemID = {} and studentId = {}", requestItemId, requestedStudentId);

            GradeBookItem existingItem = gradeBookItemMap.get(requestItemId);
            LOG.debug("Existing Item = {}", existingItem);
            if (existingItem.getItemId() == 0 && existingItem.getItemName() == null && existingItem.getItemMax() == 0) {
                LOG.debug("<---Existing Item values setting--->");

                existingItem.setItemId(requestItemId);
                existingItem.setItemName(requestedGradeBookItem.getItemName());
                existingItem.setItemMax(requestedGradeBookItem.getItemMax());
            }

            List<Student> existingStudents = existingItem.getStudents();
            LOG.debug("Existing Students = {}", existingStudents);

            if (existingStudents == null) {
                existingStudents = new ArrayList<Student>();
            }

            // checking if grade item and student record already exists
            for (Student existStudent : existingStudents) {
                if (existStudent.getId() == requestedStudentId) {
                    LOG.debug("Student already exist = {}", existStudent.getId());

                    doesExist = true;
                    LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                    LOG.debug("Cannot create Gradebook Resource as student already exists {}", content);
                    response = Response.status(Response.Status.CONFLICT).entity(content).build();
                    LOG.debug("Generated response {}", response);
                    break;
                }
            }

            // if grade item does not exists, creating it and setting it in gradeItemMap
            if (!doesExist) {
                LOG.debug("Attempting to create a student grade item resource and setting it to {}", content);

                // retrieving details of student from request
                LOG.debug("New student id to be created = {}", requestedGradeBookItem.getStudents().get(0).getId());
                Student requestedStudent = requestedGradeBookItem.getStudents().get(0);

                // creating a new student and updating the grade item
                Student newStudent = new Student();
                newStudent.setId(requestedStudentId);
                newStudent.setScore(requestedStudent.getScore());
                newStudent.setFeedback(requestedStudent.getFeedback());
                newStudent.setGraded(true);
                newStudent.setAppealStatus(Student.GradeAppeal.NOT_APPEALED);
                existingStudents.add(newStudent);
                existingItem.setStudents(existingStudents);
                gradeBookItemMap.put(requestItemId, existingItem);

                LOG.info("Creating a {} {} Status Response", Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase());
                String xmlString = Converter.convertFromObjectToXml(existingItem, GradeBookItem.class);
                URI locationURI = URI.create(context.getAbsolutePath() + "/" + requestedStudentId);
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
        LOG.debug("PathParam id = {}, {}", itemId, studentId);

        Response response = null;
        boolean doesExist = false;
        int requestedStudentId = Integer.parseInt(studentId);
        int requestedItemId = Integer.parseInt(itemId);

        try {
            GradeBookItem existingItem = gradeBookItemMap.get(requestedItemId);
            LOG.debug("Existing item id = {}, name = {}, max score = {}", existingItem.getItemId(), existingItem.getItemName(), existingItem.getItemMax());

            if (existingItem.getStudents() == null) {
                LOG.debug("Existing item students = {}", existingItem.getStudents());
                LOG.info("Creating a {} {} Status Response", Response.Status.GONE.getStatusCode(), Response.Status.GONE.getReasonPhrase());

                response = Response.status(Response.Status.GONE).entity("No Gradebook item resource to return").build();

            } else { // if grading item exists, then check if the requested student details are present

                List<Student> existingStudents = existingItem.getStudents();
                LOG.debug("Existing Students = {}", existingStudents);

                for (Student student : existingStudents) {
                    if (student.getId() == requestedStudentId) {
                        LOG.debug("Requested studentId {} found", student.getId());

                        doesExist = true;
                        LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                        break;
                    }
                }

                // if the requested student is not found
                if (!doesExist) {
                    LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());
                    response = Response.status(Response.Status.NOT_FOUND).entity("Requested student record found").build();
                } else {
                    String xmlString = Converter.convertFromObjectToXml(existingItem, GradeBookItem.class);
                    response = Response.status(Response.Status.OK).entity(xmlString).build();
                }
            }
        } catch (Exception e) {
            LOG.error("Error: " + e);
            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server error: check the server logs").build();
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
                Student oldStudent = null;

                for (Student student : existingStudents) {
                    if (student.getId() == requestedStudentId) {
                        doesExist = true;
                        student.setFeedback(updatedStudent.getFeedback());
                        student.setScore(updatedStudent.getScore());
                        oldStudent = student;
                        break;
                    }
                }

                if (doesExist) {
                    existingItem.setStudents(existingStudents);
                    gradeBookItemMap.put(requestedItemId, existingItem);

                    LOG.debug("The XML {} was converted to the object {}", content, existingItem);
                    LOG.debug("Updated Gradebook student resource to {}", oldStudent);
                    LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());

                    String xmlString = Converter.convertFromObjectToXml(oldStudent, Student.class);
                    response = Response.status(Response.Status.OK).entity(xmlString).build();

                } else {
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
    public Response deleteGradeBookItem(@PathParam("itemId") String itemId, @PathParam("sid") String studentId) {
        LOG.debug("DELETE request");
        LOG.debug("PathParam id = {}", itemId + ", " + studentId);

        Response response = null;
        boolean doesExist = false;
        int requestedStudentId = Integer.parseInt(studentId);
        int requestedItemId = Integer.parseInt(itemId);

        try {
            GradeBookItem existingItem = gradeBookItemMap.get(requestedItemId);
            if (existingItem == null) {
                LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                LOG.debug("GradeBook item resource to delete does not exist");

                response = Response.status(Response.Status.CONFLICT).entity("GradeBook item resource to delete does not exist").build();

            } else { // if grading item exists, then check if the requested old student details are present

                List<Student> existingStudents = existingItem.getStudents();
                for (Student student : existingStudents) {
                    if (student.getId() == requestedStudentId) {
                        doesExist = true;
                        LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                        LOG.debug("Retrieving the student resource {}", student.getId());
                        break;
                    }
                }

                if (doesExist) {
                    existingStudents.remove(requestedStudentId);
                    existingItem.setStudents(existingStudents);
                    gradeBookItemMap.put(requestedItemId, existingItem);
                    response = Response.status(Response.Status.OK).build();
                } else {
                    LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());
                    response = Response.status(Response.Status.NOT_FOUND).entity("No Gradebook Resource to return").build();
                }
            }
        } catch (Exception e) {
            LOG.error("Error: " + e);
            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Requested resource not found").build();
        }

        LOG.debug("Generated response {}", response);
        return response;
    }
}
