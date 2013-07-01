package org.rulez.magwas.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XsltTest {
    
    @Test
    public void testXslt() throws IOException, SAXException,
            TransformerException {
        
        FileInputStream htmlInputStream = new FileInputStream(
                "src/test/resources/jstests.html");
        String htmlString = IOUtils.toString(htmlInputStream);
        htmlInputStream.close();
        
        TransformerFactory tfactory = TransformerFactory.newInstance();
        
        Transformer transformer = tfactory.newTransformer(new StreamSource(
                new File("src/main/webapp/stylesheet.xsl")));
        
        StringWriter out = new StringWriter();
        
        transformer.transform(new StreamSource(new File(
                "src/test/resources/searchtest.xml")), new StreamResult(out));
        
        String xmlString = out.toString();
        htmlString = htmlString.replaceAll("(?s)BEGIN[^E]*END", "");
        htmlString = htmlString.replaceAll("\\.\\.\\/\\.\\./main\\/", "");
        htmlString = htmlString.replaceAll("\\.\\.\\/", "");
        htmlString = htmlString.replaceAll("webapp/worldmodel.css",
                "worldmodel.css");
        htmlString = htmlString.replaceAll("<!--// testing-->\n", "");
        htmlString = htmlString.replaceAll(
                "<link rel=\"stylesheet\" href=\"qunit.css\">\n", "");
        xmlString = xmlString.replaceAll("(?s)BEGIN[^E]*END", "");
        
        PrintWriter writer1 = new PrintWriter("/tmp/foo1", "UTF-8");
        writer1.println(xmlString);
        writer1.close();
        PrintWriter writer2 = new PrintWriter("/tmp/foo2", "UTF-8");
        writer2.println(htmlString);
        writer2.close();
        
        assertEquals(xmlString, htmlString);
    }
}
