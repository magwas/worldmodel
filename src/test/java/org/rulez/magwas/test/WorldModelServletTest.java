package org.rulez.magwas.test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.rulez.magwas.worldmodel.Util;
import org.rulez.magwas.worldmodel.WorldModelServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WorldModelServletTest {
    
    private WorldModelServlet servlet;
    
    @Before
    public void before() throws Exception {
        Session session = Util.getSession();
        Transaction tx = session.beginTransaction();
        session.createQuery("delete from BaseObject").executeUpdate();
        tx.commit();
        session.close();
        System.out.println("Before");
        servlet = new WorldModelServlet();
        ServletConfig config = new MockServletConfig();
        servlet.init(config);
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        servlet.doGet(request, response);
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring);
        TestUtil.assertExpressionOnXmlString("count(//BaseObject) >= 15",
                retstring);
        
        FileInputStream inputStream = new FileInputStream(
                "src/test/resources/searchtest.xml");
        String objstring = IOUtils.toString(inputStream);
        inputStream.close();
        MyHttpServletRequest request2 = new MyHttpServletRequest();
        MyHttpServletResponse response2 = new MyHttpServletResponse();
        request2.setInputString(objstring);
        servlet.doPost(request2, response2);
        String output2 = response2.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", output2);
        
    }
    
    @Test
    public void testDoGetRightOffset() throws Exception {
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        // request.setParameter("value", "gettest1");
        // request.setParameter("offset", "2");
        
        servlet.doGet(request, response);
        String output = response.getOutput();
        
        Document retdoc = Util.newDocument(output);
        assertEquals(0, retdoc.getElementsByTagName("exception").getLength());
        assertEquals(1, retdoc.getElementsByTagName("objects").getLength());
        assertEquals(25, retdoc.getElementsByTagName("BaseObject").getLength());
        assertEquals(1, retdoc.getElementsByTagName("continues").getLength());
        
        NodeList bolist1 = retdoc.getElementsByTagName("BaseObject");
        
        MyHttpServletRequest request2 = new MyHttpServletRequest();
        MyHttpServletResponse response2 = new MyHttpServletResponse();
        request2.setParameter("offset", "15");
        
        servlet.doGet(request2, response2);
        String output2 = response2.getOutput();
        
        Document retdoc2 = Util.newDocument(output2);
        assertEquals(0, retdoc2.getElementsByTagName("exception").getLength());
        assertEquals(1, retdoc2.getElementsByTagName("objects").getLength());
        assertEquals(25, retdoc2.getElementsByTagName("BaseObject").getLength());
        assertEquals(1, retdoc2.getElementsByTagName("continues").getLength());
        
        NodeList bolist2 = retdoc2.getElementsByTagName("BaseObject");
        Element bo1 = (Element) bolist1.item(8 + 15);
        Element bo2 = (Element) bolist2.item(8);
        assertEquals(bo1.getAttribute("id"), bo2.getAttribute("id"));
        assertEquals(bo1.getAttribute("source"), bo2.getAttribute("source"));
        assertEquals(bo1.getAttribute("type"), bo2.getAttribute("type"));
        assertEquals(bo1.getAttribute("dest"), bo2.getAttribute("dest"));
        
    }
    
    @Test
    public void testDoGetFetchAllConsistency() throws Exception {
        int offset = 0;
        boolean doit = true;
        List<String> idlist = new ArrayList<String>();
        while (doit) {
            MyHttpServletRequest request = new MyHttpServletRequest();
            MyHttpServletResponse response = new MyHttpServletResponse();
            request.setParameter("offset", Integer.toString(offset));
            
            servlet.doGet(request, response);
            String output = response.getOutput();
            
            Document retdoc = Util.newDocument(output);
            assertEquals(0, retdoc.getElementsByTagName("exception")
                    .getLength());
            assertEquals(1, retdoc.getElementsByTagName("objects").getLength());
            NodeList bolist = retdoc.getElementsByTagName("BaseObject");
            for (int i = 0; i < bolist.getLength(); i++) {
                String idstr = ((Element) bolist.item(i)).getAttribute("id");
                assert (!idlist.contains(idstr));
                idlist.add(idstr);
            }
            if (0 == retdoc.getElementsByTagName("continues").getLength()) {
                doit = false;
            } else {
                offset += 25;
            }
        }
    }
    
    @Test
    public void testDoGetFetchOneType() throws Exception {
        List<String> idlist = new ArrayList<String>();
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setParameter("type", "relation");
        
        servlet.doGet(request, response);
        String output = response.getOutput();
        
        Document retdoc = Util.newDocument(output);
        assertEquals(0, retdoc.getElementsByTagName("exception").getLength());
        assertEquals(1, retdoc.getElementsByTagName("objects").getLength());
        NodeList bolist = retdoc.getElementsByTagName("BaseObject");
        for (int i = 0; i < bolist.getLength(); i++) {
            String idstr = ((Element) bolist.item(i)).getAttribute("id");
            String typestr = ((Element) bolist.item(i)).getAttribute("type");
            assert (!idlist.contains(idstr));
            assertEquals(typestr, "relation");
            idlist.add(idstr);
        }
        assertEquals(2, bolist.getLength());
        assertEquals(0, retdoc.getElementsByTagName("continues").getLength());
    }
    
    @Test
    public void testDoGetFetchOneSource() throws Exception {
        List<String> idlist = new ArrayList<String>();
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setParameter("source", "thing");
        
        servlet.doGet(request, response);
        String output = response.getOutput();
        
        Document retdoc = Util.newDocument(output);
        assertEquals(0, retdoc.getElementsByTagName("exception").getLength());
        assertEquals(1, retdoc.getElementsByTagName("objects").getLength());
        NodeList bolist = retdoc.getElementsByTagName("BaseObject");
        for (int i = 0; i < bolist.getLength(); i++) {
            String idstr = ((Element) bolist.item(i)).getAttribute("id");
            String typestr = ((Element) bolist.item(i)).getAttribute("source");
            assert (!idlist.contains(idstr));
            assertEquals(typestr, "thing");
            idlist.add(idstr);
        }
        assertEquals(1, bolist.getLength());
        assertEquals(0, retdoc.getElementsByTagName("continues").getLength());
    }
    
    @Test
    public void testDoGetFetchOneDest() throws Exception {
        List<String> idlist = new ArrayList<String>();
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setParameter("dest", "One");
        
        servlet.doGet(request, response);
        String output = response.getOutput();
        
        Document retdoc = Util.newDocument(output);
        assertEquals(0, retdoc.getElementsByTagName("exception").getLength());
        assertEquals(1, retdoc.getElementsByTagName("objects").getLength());
        NodeList bolist = retdoc.getElementsByTagName("BaseObject");
        for (int i = 0; i < bolist.getLength(); i++) {
            String idstr = ((Element) bolist.item(i)).getAttribute("id");
            String deststr = ((Element) bolist.item(i)).getAttribute("dest");
            assert (!idlist.contains(idstr));
            assertEquals(deststr, "One");
            idlist.add(idstr);
        }
        assertEquals(9, bolist.getLength());
        assertEquals(0, retdoc.getElementsByTagName("continues").getLength());
    }
    
    @Test
    public void testDoPostWithSelfReference() throws ServletException,
            IOException, XPathExpressionException,
            XPathFactoryConfigurationException, SAXException,
            ParserConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"stuff\" type=\"stuff\"/>"
                + "<BaseObject id=\"stuffplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"stuff\"/>"
                + "</xml>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring);
        TestUtil.assertExpressionOnXmlString("count(//BaseObject) = 2",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='stuff']/@type = 'stuff'", retstring);
    }
    
    @Test
    public void testDoPostWithSelfReference2() throws ServletException,
            IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xml>"
                + "<BaseObject id=\"stuff1\" type=\"stuff1\" source=\"stuff1\" dest=\"stuff1\"/>"
                + "</xml>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        
        assertEquals("<exception>object loop at stuff1</exception>",
                response.getOutput());
    }
    
    @Test
    public void testDoPostTest1() throws ServletException, IOException,
            XPathExpressionException, XPathFactoryConfigurationException,
            SAXException, ParserConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xml>"
                + "<BaseObject id=\"idnum\" value=\"érték\" type=\"thing\"/>"
                + "<BaseObject id=\"idnumplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"idnum\"/>"
                + "</xml>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring);
        TestUtil.assertExpressionOnXmlString("count(//BaseObject) = 2",
                retstring);
    }
    
    @Test
    public void testDoPostMoreObjects() throws ServletException, IOException,
            XPathExpressionException, XPathFactoryConfigurationException,
            SAXException, ParserConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xml>"
                + "<BaseObject id=\"idnum2\" value=\"érték\" type=\"thing\"/>"
                + "<BaseObject id=\"idnumplace2\" type=\"contains\" source=\"hierarchyroot\" dest=\"idnum2\"/>"
                + "<BaseObject id=\"idnum3\" value=\"érték\" type=\"thing\"/>"
                + "<BaseObject id=\"idnumplace3\" type=\"contains\" source=\"hierarchyroot\" dest=\"idnum3\"/>"
                + "</xml>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring);
        TestUtil.assertExpressionOnXmlString("count(//BaseObject) = 4",
                retstring);
    }
    
    @Test
    public void testDoPostTest2() throws ServletException, IOException,
            XPathExpressionException, XPathFactoryConfigurationException,
            SAXException, ParserConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xml>\n"
                + "<BaseObject id=\"harom\" value=\"na micsoda?\" type=\"thing\"/>"
                + "<BaseObject id=\"haromplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"harom\"/>"
                + "</xml>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring);
        TestUtil.assertExpressionOnXmlString("count(//BaseObject) = 2",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "count(//BaseObject[@id='harom']) = 1", retstring);
        TestUtil.assertExpressionOnXmlString(
                "count(//BaseObject[@id='haromplace']) = 1", retstring);
        
    }
    
    @Test
    public void testDoPostAndGet() throws ServletException, IOException,
            XPathExpressionException, XPathFactoryConfigurationException,
            SAXException, ParserConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<xml>\n"
                + "<BaseObject id=\"hehe\" value=\"post And Get\" type=\"thing\"/>"
                + "<BaseObject id=\"heheplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"hehe\"/>"
                + "</xml>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring);
        TestUtil.assertExpressionOnXmlString("count(//BaseObject) = 2",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "count(//BaseObject[@id='hehe']) = 1", retstring);
        TestUtil.assertExpressionOnXmlString(
                "count(//BaseObject[@id='heheplace']) = 1", retstring);
        MyHttpServletRequest request2 = new MyHttpServletRequest();
        MyHttpServletResponse response2 = new MyHttpServletResponse();
        request2.setParameter("id", "hehe");
        servlet.doGet(request2, response2);
        String retstring2 = response2.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring2);
        TestUtil.assertExpressionOnXmlString("count(//BaseObject) = 2",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "count(//BaseObject[@id='hehe']) = 1", retstring2);
    }
    
    @Test
    public void testDoPostTestDoubleWrite() throws ServletException,
            IOException, XPathExpressionException,
            XPathFactoryConfigurationException, SAXException,
            ParserConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<xml>\n"
                + "<BaseObject id=\"negy\" value=\"negyedik\" type=\"thing\"/>"
                + "<BaseObject id=\"negyplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"negy\"/>"
                + "</xml>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring);
        TestUtil.assertExpressionOnXmlString("count(//BaseObject) = 2",
                retstring);
        MyHttpServletRequest request2 = new MyHttpServletRequest();
        MyHttpServletResponse response2 = new MyHttpServletResponse();
        request2.setInputString(objstring);
        servlet.doPost(request2, response2);
        assertEquals(
                "<exception>there is already an object with this id: negy</exception>",
                response2.getOutput());
    }
    
}
