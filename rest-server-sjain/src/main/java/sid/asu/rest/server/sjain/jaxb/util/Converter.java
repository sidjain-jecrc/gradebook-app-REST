/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid.asu.rest.server.sjain.jaxb.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringWriter;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Siddharth
 * Code Reference taken from Professor Frank Callis
 */
public class Converter {

    private static final Logger LOG = LoggerFactory.getLogger(Converter.class);

    public static Object convertFromXmlToObject(Object xmlString, Class... type) throws JAXBException {
        LOG.info("Converting from XML to an Object");
        LOG.debug("Object xmlString = {}", xmlString);
        LOG.debug("Class... type = {}", (Object) type);

        Object result;

        StringReader sr = null;

        if (xmlString instanceof String) {
            sr = new StringReader((String) xmlString);
        }

        JAXBContext context = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        result = unmarshaller.unmarshal(sr);

        return result;
    }

    public static String convertFromObjectToXml(Object source, Class... type) {
        LOG.info("Converting from and Object to XML");
        LOG.debug("Object source = {}", source);
        LOG.debug("Class... type = {}", (Object) type);

        String result;
        StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(source, sw);
            result = sw.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
