/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid.asu.rest.client.sjain.restclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Siddharth
 */
public class GradeBook_CRUD_Client {

    private static final Logger LOG = LoggerFactory.getLogger(GradeBook_CRUD_Client.class);

    private WebResource webResource;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/rest-server-sjain/webapi";

    public GradeBook_CRUD_Client() {
        LOG.info("Creating a GradeBook_CRUD_Client REST client");

        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(BASE_URI).path("gradebook");
        LOG.debug("webResource = {}", webResource.getURI());
    }

    public ClientResponse createGradeBookItem(Object requestEntity) throws UniformInterfaceException {
        LOG.info("Initiating a Create request");

        return webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, requestEntity);
    }

    public ClientResponse deleteGradeBookItem(String itemId, String sid) throws UniformInterfaceException {
        LOG.info("Initiating a Delete request");
        LOG.debug("itemId = {}", itemId);
        LOG.debug("sid = {}", sid);
        String queryParams = itemId + "/" + sid;

        return webResource.path(queryParams).delete(ClientResponse.class);
    }

    public ClientResponse updateGradeBookItem(Object requestEntity, String id) throws UniformInterfaceException {
        LOG.info("Initiating an Update request");
        LOG.debug("XML String = {}", (String) requestEntity);
        LOG.debug("Id = {}", id);

        return webResource.path(id).type(MediaType.APPLICATION_XML).put(ClientResponse.class, requestEntity);
    }

    public <T> T retrieveGradeBookItem(Class<T> responseType, String itemId, String sid) throws UniformInterfaceException {
        LOG.info("Initiating a Retrieve request");
        LOG.debug("responseType = {}", responseType.getClass());
        LOG.debug("itemId = {}", itemId);
        LOG.debug("sid = {}", sid);
        String queryParams = itemId + "/" + sid;

        return webResource.path(queryParams).accept(MediaType.APPLICATION_XML).get(responseType);
    }

    public void close() {
        LOG.info("Closing the REST Client");
        client.destroy();
    }

}
