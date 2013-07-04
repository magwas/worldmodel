package org.rulez.magwas.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import net.sf.saxon.lib.NamespaceConstant;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rulez.magwas.worldmodel.BaseObject;
import org.rulez.magwas.worldmodel.InputParseException;
import org.rulez.magwas.worldmodel.Util;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class TestUtil {
    
    public static String NormalizeXmlString(String in) {// FIXME testcases for
                                                        // stylesheet and xml
                                                        // header
        return in
                .replaceAll("<.xml version=\"1.0\" encoding=\"UTF-8\".*?>", "")
                .replaceAll("\n", "")
                .replace(
                        "<?xml-stylesheet type=\"text/xsl\" href=\"stylesheet.xsl\"?>",
                        "");
    }
    
    public static void assertExpressionOnObject(String expression,
            BaseObject obj) throws InputParseException, SAXException,
            IOException, ParserConfigurationException,
            XPathExpressionException, XPathFactoryConfigurationException,
            TransformerException {
        assertExpressionOnXmlString(expression, obj.toXmlString());
    }
    
    public static void assertExpressionOnXmlString(String expression, String xml)
            throws SAXException, IOException, ParserConfigurationException,
            XPathExpressionException, XPathFactoryConfigurationException {
        System.setProperty("javax.xml.xpath.XPathFactory:"
                + NamespaceConstant.OBJECT_MODEL_SAXON,
                "net.sf.saxon.xpath.XPathFactoryImpl");
        XPathFactory xpf = XPathFactory
                .newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
        Document doc = Util.newDocument(xml);
        XPath xPath = xpf.newXPath();
        String result = xPath.compile(expression).evaluate(doc);
        if (!result.equals("true")) {
            fail("got '" + result + "' for \"" + expression + "\" on:\n" + xml);
        }
    }
    
    private static String codes = "0123456789abcdef";
    private static Logger logger1;
    private static Logger logger2;
    private static Logger logger3;
    
    public static void hexdump(String label, String str) {
        char[] arr = str.toCharArray();
        
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            // yeah, hexdump is magical, thus uses magic numbers
            buff.append(codes.charAt((arr[i] >> 8) & 0x0f))
                    .append(codes.charAt((arr[i] >> 4) & 0x0f))
                    .append(codes.charAt(arr[i] & 0xf)).append(arr[i])
                    .append(" ");
        }
        Util.debug(label + buff.toString());
    }
    
    public static void turnLoggingOn() {
        logger1 = Logger.getLogger("org.hibernate.SQL");
        logger1.setLevel(Level.TRACE);
        logger2 = Logger
                .getLogger("org.hibernate.type.descriptor.sql.BasicBinder");
        logger2.setLevel(Level.TRACE);
        logger3 = Logger.getLogger("org.rulez.magwas.worldmodel");
        logger3.setLevel(Level.DEBUG);
        Util.debug("Here comes the problem");
    }
    
    public static void turnLoggingOff() {
        
        Util.debug("End of problem");
        
        logger1.setLevel(Level.INFO);
        logger2.setLevel(Level.INFO);
        logger3.setLevel(Level.INFO);
    }
    
}
