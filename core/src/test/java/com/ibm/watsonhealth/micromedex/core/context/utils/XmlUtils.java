package com.ibm.watsonhealth.micromedex.core.context.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {

    private XmlUtils() {
    }

    public static String format(final String xml) {
        try {
            final Document document = parseXmlFile(xml);
            final OutputFormat format = new OutputFormat(document);
            format.setLineWidth(500);
            format.setIndenting(true);
            format.setIndent(2);
            final Writer out = new StringWriter();
            final XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (final ParserConfigurationException | IOException | SAXException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Document parseXmlFile(final String in) throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final InputSource is = new InputSource(new StringReader(in));
        return db.parse(is);
    }

}
