package org.rulez.magwas.test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rulez.magwas.worldmodel.BaseObject;
import org.rulez.magwas.worldmodel.Util;
import org.rulez.magwas.worldmodel.WorldModelServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WorldModelServletTest {
    
    private static WorldModelServlet servlet;
    
    @BeforeClass
    public static void before() throws Exception {
        System.out.println("Before");
        servlet = new WorldModelServlet();
        ServletConfig config = new MockServletConfig();
        servlet.init(config);
        Session session = Util.getSession();
        BaseObject contains = BaseObject.getBaseObjectByCompositeId("contains",
                session);
        BaseObject contains2 = BaseObject.getBaseObjectByCompositeId(
                "contains", session);
        session.close();
        assert (contains != null);
        assertEquals(contains, contains2);
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        servlet.doGet(request, response);
        String output = response.getOutput();
        assertEquals(
                "<objects>"
                        + "<BaseObject id=\"thing\"/>"
                        + "<BaseObject dest=\"thing\" id=\"relation\" source=\"thing\" type=\"thing\"/>"
                        + "<BaseObject id=\"contains\" type=\"relation\"/>"
                        + "<BaseObject id=\"folder\" type=\"thing\"/>"
                        + "<BaseObject id=\"hierarchyroot\" type=\"folder\"/>"
                        + "<BaseObject id=\"ontology\" type=\"folder\"/>"
                        + "<BaseObject id=\"basic ontology\" type=\"folder\"/>"
                        + "<BaseObject dest=\"ontology\" id=\"c1\" source=\"hierarchyroot\" type=\"contains\"/>"
                        + "<BaseObject dest=\"basic ontology\" id=\"c2\" source=\"ontology\" type=\"contains\"/>"
                        + "<BaseObject dest=\"thing\" id=\"c3\" source=\"basic ontology\" type=\"contains\"/>"
                        + "<BaseObject dest=\"relation\" id=\"c4\" source=\"basic ontology\" type=\"contains\"/>"
                        + "<BaseObject id=\"hierarchy\" type=\"folder\"/>"
                        + "<BaseObject dest=\"hierarchy\" id=\"c5\" source=\"ontology\" type=\"contains\"/>"
                        + "<BaseObject dest=\"contains\" id=\"c6\" source=\"hierarchy\" type=\"contains\"/>"
                        + "<BaseObject dest=\"folder\" id=\"c7\" source=\"hierarchy\" type=\"contains\"/>"
                        + "</objects>", TestUtil.NormalizeXmlString(output));
        servlet.checkAll();
        FileInputStream inputStream = new FileInputStream(
                "src/test/resources/searchtest.xml");
        String objstring = IOUtils.toString(inputStream);
        inputStream.close();
        MyHttpServletRequest request2 = new MyHttpServletRequest();
        MyHttpServletResponse response2 = new MyHttpServletResponse();
        request2.setInputString(objstring);
        servlet.doPost(request2, response2);
        String output2 = response2.getOutput();
        Document retdoc = Util.newDocument(output2);
        assertEquals(0, retdoc.getElementsByTagName("exception").getLength());
        
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
            IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"stuff\" type=\"stuff\"/>"
                + "<BaseObject id=\"stuffplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"stuff\"/>"
                + "</xml>";
        String retstring = "<objects>"
                + "<BaseObject id=\"stuff\" type=\"stuff\"/>"
                + "<BaseObject dest=\"stuff\" id=\"stuffplace\" source=\"hierarchyroot\" type=\"contains\"/>"
                + "</objects>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
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
    public void testDoPostTest1() throws ServletException, IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xml>"
                + "<BaseObject id=\"idnum\" value=\"érték\" type=\"thing\"/>"
                + "<BaseObject id=\"idnumplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"idnum\"/>"
                + "</xml>";
        String retstring = "<objects>"
                + "<BaseObject id=\"idnum\" type=\"thing\" value=\"érték\"/>"
                + "<BaseObject dest=\"idnum\" id=\"idnumplace\" source=\"hierarchyroot\" type=\"contains\"/>"
                + "</objects>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
    }
    
    @Test
    public void testDoPostMoreObjects() throws ServletException, IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xml>"
                + "<BaseObject id=\"idnum2\" value=\"érték\" type=\"thing\"/>"
                + "<BaseObject id=\"idnumplace2\" type=\"contains\" source=\"hierarchyroot\" dest=\"idnum2\"/>"
                + "<BaseObject id=\"idnum3\" value=\"érték\" type=\"thing\"/>"
                + "<BaseObject id=\"idnumplace3\" type=\"contains\" source=\"hierarchyroot\" dest=\"idnum3\"/>"
                + "</xml>";
        String retstring = "<objects>"
                + "<BaseObject id=\"idnum2\" type=\"thing\" value=\"érték\"/>"
                + "<BaseObject dest=\"idnum2\" id=\"idnumplace2\" source=\"hierarchyroot\" type=\"contains\"/>"
                + "<BaseObject id=\"idnum3\" type=\"thing\" value=\"érték\"/>"
                + "<BaseObject dest=\"idnum3\" id=\"idnumplace3\" source=\"hierarchyroot\" type=\"contains\"/>"
                + "</objects>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
    }
    
    @Test
    public void testDoPostTest2() throws ServletException, IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xml>\n"
                + "<BaseObject id=\"harom\" value=\"na micsoda?\" type=\"thing\"/>"
                + "<BaseObject id=\"haromplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"harom\"/>"
                + "</xml>";
        String retstring = "<objects>"
                + "<BaseObject id=\"harom\" type=\"thing\" value=\"na micsoda?\"/>"
                + "<BaseObject dest=\"harom\" id=\"haromplace\" source=\"hierarchyroot\" type=\"contains\"/>"
                + "</objects>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
    }
    
    @Test
    public void testDoPostAndGet() throws ServletException, IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<xml>\n"
                + "<BaseObject id=\"hehe\" value=\"post And Get\" type=\"thing\"/>"
                + "<BaseObject id=\"heheplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"hehe\"/>"
                + "</xml>";
        String retstring = "<objects>"
                + "<BaseObject id=\"hehe\" type=\"thing\" value=\"post And Get\"/>"
                + "<BaseObject dest=\"hehe\" id=\"heheplace\" source=\"hierarchyroot\" type=\"contains\"/>"
                + "</objects>";
        String retstring2 = "<objects>"
                + "<BaseObject id=\"hehe\" type=\"thing\" value=\"post And Get\"/>"
                + "</objects>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
        MyHttpServletRequest request2 = new MyHttpServletRequest();
        MyHttpServletResponse response2 = new MyHttpServletResponse();
        request2.setParameter("id", "hehe");
        servlet.doGet(request2, response2);
        assertEquals(retstring2,
                TestUtil.NormalizeXmlString(response2.getOutput()));
    }
    
    @Test
    public void testDoPostTestDoubleWrite() throws ServletException,
            IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<xml>\n"
                + "<BaseObject id=\"negy\" value=\"negyedik\" type=\"thing\"/>"
                + "<BaseObject id=\"negyplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"negy\"/>"
                + "</xml>";
        String retstring = "<objects>"
                + "<BaseObject id=\"negy\" type=\"thing\" value=\"negyedik\"/>"
                + "<BaseObject dest=\"negy\" id=\"negyplace\" source=\"hierarchyroot\" type=\"contains\"/>"
                + "</objects>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
        MyHttpServletRequest request2 = new MyHttpServletRequest();
        MyHttpServletResponse response2 = new MyHttpServletResponse();
        request2.setInputString(objstring);
        servlet.doPost(request2, response2);
        assertEquals(
                "<exception>there is already an object with this id: negy</exception>",
                response2.getOutput());
    }
    
}
