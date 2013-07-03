package org.rulez.magwas.test;

import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import net.sf.saxon.lib.NamespaceConstant;

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
            XPathExpressionException, XPathFactoryConfigurationException {
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
}
